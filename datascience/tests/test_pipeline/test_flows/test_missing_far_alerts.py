from datetime import datetime, timedelta
from unittest.mock import patch

import pandas as pd
from geoalchemy2 import Geometry
from sqlalchemy import BOOLEAN, FLOAT, TIMESTAMP, VARCHAR, Column, MetaData, Table

from src.pipeline.flows.missing_far_alerts import (
    concat,
    extract_vessels_that_emitted_fars,
    flow,
    get_dates,
    get_vessels_with_missing_fars,
    make_alerts,
    make_vessels_at_sea_query,
)
from src.read_query import read_query
from tests.mocks import mock_datetime_utcnow


@patch(
    "src.pipeline.flows.missing_far_alerts.datetime",
    mock_datetime_utcnow(datetime(2021, 1, 1, 16, 10, 0)),
)
def tast_get_dates():
    yesterday_at_zero_hours, today_at_zero_hours, utcnow = get_dates.run()
    assert yesterday_at_zero_hours == datetime(2020, 12, 31, 0, 0, 0)
    assert today_at_zero_hours == datetime(2021, 1, 1, 0, 0, 0)
    assert utcnow == datetime(2021, 1, 1, 16, 10, 0)


def test_make_vessels_at_sea_query():

    # Setup

    from_date = datetime(2020, 12, 4, 12, 23, 0)
    to_date = datetime(2020, 12, 5, 12, 23, 0)

    meta = MetaData()

    facade_areas_table = Table(
        "facades", meta, Column("facade", VARCHAR), Column("geometry", Geometry)
    )

    positions_table = Table(
        "positions",
        meta,
        Column("internal_reference_number", VARCHAR),
        Column("external_reference_number", VARCHAR),
        Column("ircs", VARCHAR),
        Column("vessel_name", VARCHAR),
        Column("flag_state", VARCHAR),
        Column("date_time", TIMESTAMP),
        Column("is_at_port", BOOLEAN),
        Column("geometry", Geometry),
    )

    vessels_table = Table(
        "vessels", meta, Column("cfr", VARCHAR), Column("length", FLOAT)
    )

    eez_areas_table = Table(
        "eez_areas", meta, Column("wkb_geometry", Geometry), Column("iso_sov1", VARCHAR)
    )

    # Test with all arguments

    query = make_vessels_at_sea_query.run(
        positions_table=positions_table,
        facade_areas_table=facade_areas_table,
        from_date=from_date,
        to_date=to_date,
        states_to_monitor_iso2=["ES"],
        vessels_table=vessels_table,
        minimum_length=12.0,
        eez_areas_table=eez_areas_table,
        eez_to_monitor_iso3=["FRA"],
    )

    query_string = str(query.compile(compile_kwargs={"literal_binds": True}))
    expected_query_string = (
        "SELECT DISTINCT "
        "positions.internal_reference_number AS cfr, "
        "positions.external_reference_number AS external_immatriculation, "
        "positions.ircs, "
        "positions.vessel_name, "
        "positions.flag_state, "
        "facades.facade "
        "\nFROM positions "
        "LEFT OUTER JOIN facades "
        "ON ST_Intersects(positions.geometry, facades.geometry) "
        "JOIN vessels "
        "ON positions.internal_reference_number = vessels.cfr "
        "JOIN eez_areas "
        "ON ST_Intersects(positions.geometry, eez_areas.wkb_geometry) "
        "\nWHERE "
        "positions.date_time >= '2020-12-04 12:23:00' AND "
        "positions.date_time < '2020-12-05 12:23:00' AND "
        "positions.internal_reference_number IS NOT NULL AND "
        "NOT positions.is_at_port AND "
        "positions.flag_state IN ('ES') AND "
        "vessels.length >= 12.0 AND "
        "eez_areas.iso_sov1 IN ('FRA')"
    )

    assert query_string == expected_query_string

    # Test without optional arguments

    query = make_vessels_at_sea_query.run(
        positions_table=positions_table,
        facade_areas_table=facade_areas_table,
        from_date=from_date,
        to_date=to_date,
    )

    query_string = str(query.compile(compile_kwargs={"literal_binds": True}))
    expected_query_string = (
        "SELECT DISTINCT "
        "positions.internal_reference_number AS cfr, "
        "positions.external_reference_number AS external_immatriculation, "
        "positions.ircs, "
        "positions.vessel_name, "
        "positions.flag_state, "
        "facades.facade "
        "\nFROM positions "
        "LEFT OUTER JOIN facades "
        "ON ST_Intersects(positions.geometry, facades.geometry) "
        "\nWHERE "
        "positions.date_time >= '2020-12-04 12:23:00' AND "
        "positions.date_time < '2020-12-05 12:23:00' AND "
        "positions.internal_reference_number IS NOT NULL AND "
        "NOT positions.is_at_port"
    )

    assert query_string == expected_query_string


def test_extract_vessels_that_emitted_fars(reset_test_data):

    now = datetime.utcnow()

    vessels_that_emitted_fars = extract_vessels_that_emitted_fars.run(
        declaration_min_datetime_utc=now - timedelta(days=2),
        declaration_max_datetime_utc=now - timedelta(days=1),
        fishing_operation_min_datetime_utc=datetime(year=2018, month=7, day=21),
        fishing_operation_max_datetime_utc=datetime(year=2018, month=7, day=22),
    )
    assert vessels_that_emitted_fars == {"ABC000306959"}

    vessels_that_emitted_fars = extract_vessels_that_emitted_fars.run(
        declaration_min_datetime_utc=now - timedelta(days=2),
        declaration_max_datetime_utc=now - timedelta(days=1),
        fishing_operation_min_datetime_utc=datetime(year=2015, month=7, day=21),
        fishing_operation_max_datetime_utc=datetime(year=2015, month=7, day=22),
    )
    assert vessels_that_emitted_fars == set()

    vessels_that_emitted_fars = extract_vessels_that_emitted_fars.run(
        declaration_min_datetime_utc=now - timedelta(days=5),
        declaration_max_datetime_utc=now - timedelta(days=4),
        fishing_operation_min_datetime_utc=datetime(year=2018, month=7, day=21),
        fishing_operation_max_datetime_utc=datetime(year=2018, month=7, day=22),
    )
    assert vessels_that_emitted_fars == set()

    vessels_that_emitted_fars = extract_vessels_that_emitted_fars.run(
        declaration_min_datetime_utc=now - timedelta(weeks=2),
        declaration_max_datetime_utc=now - timedelta(days=1),
        fishing_operation_min_datetime_utc=datetime(year=2018, month=7, day=21),
        fishing_operation_max_datetime_utc=datetime(year=2018, month=7, day=22),
    )

    assert vessels_that_emitted_fars == {"ABC000306959", "ABC000542519"}


def test_concat():
    df1 = pd.DataFrame(
        {
            "cfr": ["A"],
            "external_immatriculation": ["AA"],
            "ircs": ["AAA"],
            "vessel_name": ["Vessel_A"],
            "flag_state": ["FR"],
            "facade": ["NAMO"],
        }
    )

    df2 = pd.DataFrame(
        {
            "cfr": ["B"],
            "external_immatriculation": ["BB"],
            "ircs": ["BBB"],
            "vessel_name": ["Vessel_B"],
            "flag_state": ["BE"],
            "facade": ["MEMN"],
        }
    )

    expected_res = pd.DataFrame(
        {
            "cfr": ["A", "B"],
            "external_immatriculation": ["AA", "BB"],
            "ircs": ["AAA", "BBB"],
            "vessel_name": ["Vessel_A", "Vessel_B"],
            "flag_state": ["FR", "BE"],
            "facade": ["NAMO", "MEMN"],
        }
    )

    res = concat.run(df1, df2)

    pd.testing.assert_frame_equal(res, expected_res)


def test_get_vessels_with_missing_fars():
    vessels_at_sea = pd.DataFrame(
        {
            "cfr": ["Vessel_1", "Vessel_3"],
            "facade": ["NAMO", "MEMN"],
            "other_data": ["what", "ever"],
        }
    )
    vessels_that_emitted_fars = {"Vessel_1", "Vessel_2"}
    vessels_with_missing_fars = get_vessels_with_missing_fars.run(
        vessels_at_sea=vessels_at_sea,
        vessels_that_emitted_fars=vessels_that_emitted_fars,
    )

    expected_vessels_with_missing_fars = pd.DataFrame(
        {
            "cfr": ["Vessel_3"],
            "facade": ["MEMN"],
            "other_data": ["ever"],
        }
    )

    pd.testing.assert_frame_equal(
        vessels_with_missing_fars, expected_vessels_with_missing_fars
    )


def test_make_alerts():
    vessels_with_missing_fars = pd.DataFrame(
        {
            "cfr": ["A", "B"],
            "external_immatriculation": ["AA", "BB"],
            "ircs": ["AAA", "BBB"],
            "vessel_name": ["Vessel_A", "Vessel_B"],
            "flag_state": ["FR", "BE"],
            "facade": ["NAMO", "MEMN"],
            "vessel_identifier": [
                "INTERNAL_REFERENCE_NUMBER",
                "INTERNAL_REFERENCE_NUMBER",
            ],
            "risk_factor": [1.23, 3.56],
        }
    )

    alerts = make_alerts.run(
        vessels_with_missing_fars=vessels_with_missing_fars,
        alert_type="MISSING_FAR_ALERT",
        alert_config_name="MISSING_FAR_ALERT",
        creation_date=datetime(2020, 5, 3, 8, 0, 0),
    )

    expected_alerts = pd.DataFrame(
        {
            "vessel_name": ["Vessel_A", "Vessel_B"],
            "internal_reference_number": ["A", "B"],
            "external_reference_number": ["AA", "BB"],
            "ircs": ["AAA", "BBB"],
            "vessel_identifier": [
                "INTERNAL_REFERENCE_NUMBER",
                "INTERNAL_REFERENCE_NUMBER",
            ],
            "creation_date": [
                datetime(2020, 5, 3, 8, 0, 0),
                datetime(2020, 5, 3, 8, 0, 0),
            ],
            "value": [
                {
                    "seaFront": "NAMO",
                    "flagState": "FR",
                    "type": "MISSING_FAR_ALERT",
                    "riskFactor": 1.23,
                },
                {
                    "seaFront": "MEMN",
                    "flagState": "BE",
                    "type": "MISSING_FAR_ALERT",
                    "riskFactor": 3.56,
                },
            ],
            "alert_config_name": ["MISSING_FAR_ALERT", "MISSING_FAR_ALERT"],
        }
    )

    pd.testing.assert_frame_equal(alerts, expected_alerts)


def test_flow(reset_test_data):

    initial_pending_alerts = read_query(
        "monitorfish_remote", "SELECT * FROM pending_alerts"
    )

    state = flow.run(
        alert_type="MISSING_FAR_ALERT",
        alert_config_name="MISSING_FAR_ALERT",
        states_iso2_to_monitor_everywhere=["FR", "NL"],
        states_iso2_to_monitor_in_french_eez=["ES", "DE"],
        minimum_length=12.0,
    )

    assert state.is_successful()

    final_pending_alerts = read_query(
        "monitorfish_remote", "SELECT * FROM pending_alerts"
    )

    assert len(initial_pending_alerts) == 1
    assert len(final_pending_alerts) == 2
    assert "ABC000055481" in final_pending_alerts.internal_reference_number.values
    assert "MISSING_FAR_ALERT" in final_pending_alerts.alert_config_name.values
