from copy import deepcopy
from unittest.mock import patch

import pandas as pd
import pytest

from config import TEST_DATA_LOCATION
from src.pipeline.flows.logbook import (
    LogbookTransmissionFormat,
    LogbookZippedFileType,
    clean,
    extract_xmls_from_zipfile,
    extract_zipfiles,
    get_logbook_zipped_file_type,
    parse_xmls,
)

ZIPFILES_TEST_DATA_LOCATION = TEST_DATA_LOCATION / "logbook/zipfiles/"
XML_FILES_TEST_DATA_LOCATION = TEST_DATA_LOCATION / "logbook/xml_files"


def test_get_logbook_zipped_file_type():
    assert (
        get_logbook_zipped_file_type("UN_JBE202001123614.zip")
        == LogbookZippedFileType.UN
    )
    assert (
        get_logbook_zipped_file_type("ERS3_JBE202102365445.zip")
        == LogbookZippedFileType.ERS3
    )
    assert (
        get_logbook_zipped_file_type("ERS3_ACK_JBE202102365445.zip")
        == LogbookZippedFileType.ERS3_ACK
    )

    with pytest.raises(ValueError):
        get_logbook_zipped_file_type("Unexpected_filename_JBE123456789012.zip")
    with pytest.raises(ValueError):
        get_logbook_zipped_file_type("Unexpectedfilename")


@patch("src.pipeline.flows.logbook.move")
def test_extract_zipfiles_does_not_return_more_than_two_hundred_files(mock_move):

    TEST_DIRECTORY = (
        ZIPFILES_TEST_DATA_LOCATION / "test_extract_zipfiles/many_zipfiles/"
    )
    zipfiles = extract_zipfiles.run(
        input_dir=TEST_DIRECTORY / "received",
        treated_dir=TEST_DIRECTORY / "treated",
        error_dir=TEST_DIRECTORY / "error",
    )

    assert len(zipfiles) == 200
    assert set(zipfiles[0].keys()) == {
        "error_dir",
        "treated_dir",
        "full_name",
        "input_dir",
        "transmission_format",
    }

    mock_move.assert_not_called()


@patch("src.pipeline.flows.logbook.move")
def test_extract_zipfiles_handles_flux_ers3_and_unexpected_files(mock_move):

    TEST_DIRECTORY = (
        ZIPFILES_TEST_DATA_LOCATION / "test_extract_zipfiles/sample_zipfiles/"
    )

    zipfiles = extract_zipfiles.run(
        input_dir=TEST_DIRECTORY / "received",
        treated_dir=TEST_DIRECTORY / "treated",
        error_dir=TEST_DIRECTORY / "error",
    )

    assert mock_move.call_count == 2

    mock_move.assert_any_call(
        TEST_DIRECTORY / "received/2021/1/unexpected_non_zipfile.txt",
        TEST_DIRECTORY / "error/2021/1",
        if_exists="replace",
    )

    mock_move.assert_any_call(
        TEST_DIRECTORY / "received/2021/1/unexpected_zipfile_JBE123456789012.zip",
        TEST_DIRECTORY / "error/2021/1",
        if_exists="replace",
    )

    assert len(zipfiles) == 5
    assert {zipfile["input_dir"] for zipfile in zipfiles} == {
        TEST_DIRECTORY / "received/2021/1"
    }
    assert {zipfile["treated_dir"] for zipfile in zipfiles} == {
        TEST_DIRECTORY / "treated/2021/1"
    }
    assert {zipfile["error_dir"] for zipfile in zipfiles} == {
        TEST_DIRECTORY / "error/2021/1"
    }

    assert {
        zipfile["full_name"]: zipfile["transmission_format"] for zipfile in zipfiles
    } == {
        "UN_JBE202101123004.zip": LogbookTransmissionFormat.FLUX,
        "ERS3_JBE202101123000.zip": LogbookTransmissionFormat.ERS3,
        "ERS3_ACK_JBE202101123003.zip": LogbookTransmissionFormat.ERS3,
        "ERS3_JBE202101123002.zip": LogbookTransmissionFormat.ERS3,
        "ERS3_JBE202101123001.zip": LogbookTransmissionFormat.ERS3,
    }


def test_extract_xmls_from_ers3_zipfile():
    TEST_DIRECTORY = ZIPFILES_TEST_DATA_LOCATION / "test_extract_xmls"
    dummy_ERS3_zipfile = {
        "full_name": "ERS3_JBE123456789012.zip",
        "input_dir": TEST_DIRECTORY / "received",
        "treated_dir": TEST_DIRECTORY / "treated",
        "error_dir": TEST_DIRECTORY / "error",
        "transmission_format": LogbookTransmissionFormat.ERS3,
    }

    xmls = extract_xmls_from_zipfile.run(dummy_ERS3_zipfile)
    expected_xmls = deepcopy(dummy_ERS3_zipfile)
    expected_xmls["xml_messages"] = ["This is an ERS3 message."]
    assert xmls == expected_xmls


def test_extract_xmls_from_ers3_ack_zipfile():
    TEST_DIRECTORY = ZIPFILES_TEST_DATA_LOCATION / "test_extract_xmls"
    dummy_ERS3_ACK_zipfile = {
        "full_name": "ERS3_ACK_JBE123456789012.zip",
        "input_dir": TEST_DIRECTORY / "received",
        "treated_dir": TEST_DIRECTORY / "treated",
        "error_dir": TEST_DIRECTORY / "error",
        "transmission_format": LogbookTransmissionFormat.ERS3,
    }

    xmls = extract_xmls_from_zipfile.run(dummy_ERS3_ACK_zipfile)

    expected_xmls = deepcopy(dummy_ERS3_ACK_zipfile)
    expected_xmls["xml_messages"] = ["This is an ERS3_ACK message."]
    assert xmls == expected_xmls


def test_extract_xmls_from_un_zipfile():
    TEST_DIRECTORY = ZIPFILES_TEST_DATA_LOCATION / "test_extract_xmls"
    dummy_UN_zipfile = {
        "full_name": "UN_JBE123456789012.zip",
        "input_dir": TEST_DIRECTORY / "received",
        "treated_dir": TEST_DIRECTORY / "treated",
        "error_dir": TEST_DIRECTORY / "error",
        "transmission_format": LogbookTransmissionFormat.FLUX,
    }

    xmls = extract_xmls_from_zipfile.run(dummy_UN_zipfile)

    expected_xmls = deepcopy(dummy_UN_zipfile)
    expected_xmls["xml_messages"] = ["This is a UN message."]
    assert xmls == expected_xmls


def test_parse_xmls_parses_ers3_files():

    xml_messages = []
    with open(XML_FILES_TEST_DATA_LOCATION / "ers/OOE20200324042000.xml") as f:
        xml_messages.append(f.read())
    with open(XML_FILES_TEST_DATA_LOCATION / "ers/FAC20211018001928.xml") as f:
        xml_messages.append(f.read())

    zipfile = {
        "full_name": "zipfile_name.zip",
        "input_dir": "smome/input/dir",
        "treated_dir": "some/treated/dir",
        "error_dir": "some/error/dir",
        "transmission_format": LogbookTransmissionFormat.ERS3,
        "xml_messages": xml_messages,
    }

    parsed_zipfile = parse_xmls.run(zipfile)

    assert set(parsed_zipfile) == {
        "full_name",
        "input_dir",
        "treated_dir",
        "error_dir",
        "transmission_format",
        "logbook_reports",
        "logbook_raw_messages",
        "batch_generated_errors",
    }

    assert len(parsed_zipfile["logbook_raw_messages"]) == 2
    assert len(parsed_zipfile["logbook_reports"]) == 5
    assert (
        parsed_zipfile["logbook_reports"]["transmission_format"]
        == LogbookTransmissionFormat.ERS3
    ).all()
    assert not parsed_zipfile["batch_generated_errors"]


def test_parse_xmls_parses_flux_files():

    xml_messages = []
    with open(
        (
            XML_FILES_TEST_DATA_LOCATION / "flux/business/FLUX-FA-EU-710511 - "
            "Haul by haul recording reported daily.xml"
        )
    ) as f:
        xml_messages.append(f.read())
    with open(
        (
            XML_FILES_TEST_DATA_LOCATION / "flux/business_BASE64/FLUX-FA-EU-711101 - "
            "Arrival declaration.xml"
        )
    ) as f:
        xml_messages.append(f.read())

    zipfile = {
        "full_name": "zipfile_name.zip",
        "input_dir": "smome/input/dir",
        "treated_dir": "some/treated/dir",
        "error_dir": "some/error/dir",
        "transmission_format": LogbookTransmissionFormat.FLUX,
        "xml_messages": xml_messages,
    }

    parsed_zipfile = parse_xmls.run(zipfile)

    assert set(parsed_zipfile) == {
        "full_name",
        "input_dir",
        "treated_dir",
        "error_dir",
        "transmission_format",
        "logbook_reports",
        "logbook_raw_messages",
        "batch_generated_errors",
    }

    assert len(parsed_zipfile["logbook_raw_messages"]) == 2
    assert len(parsed_zipfile["logbook_reports"]) == 2
    assert (
        parsed_zipfile["logbook_reports"]["transmission_format"]
        == LogbookTransmissionFormat.FLUX
    ).all()
    assert not parsed_zipfile["batch_generated_errors"]


def test_clean():
    assert clean.run(None) is None

    parsed = pd.DataFrame(
        {
            "operation_number": [1, 1, 2, 3, 4, 5, 6],
            "operation_type": ["DAT", "DAT", "DEL", "COR", "RET", "RSP", "QUE"],
        }
    )

    expected_cleaned_parsed = pd.DataFrame(
        {
            "operation_number": [1, 1, 2, 3, 4],
            "operation_type": ["DAT", "DAT", "DEL", "COR", "RET"],
        }
    )

    parsed_with_xml = pd.DataFrame(
        {
            "operation_number": [1, 2, 3, 4, 5, 6],
            "operation_type": ["DAT", "DEL", "COR", "RET", "RSP", "QUE"],
        }
    )

    expected_cleaned_parsed_with_xml = pd.DataFrame(
        {
            "operation_number": [1, 2, 3, 4],
            "operation_type": ["DAT", "DEL", "COR", "RET"],
        }
    )

    zipfile = {
        "full_name": "dummy.zip",
        "input_dir": "dummy_input_dir",
        "treated_dir": "dummy_treated_dir",
        "non_treated_dir": "dummy_non_treated_dir",
        "error_dir": "dummy_error_dir",
        "parsed": parsed,
        "parsed_with_xml": parsed_with_xml,
        "batch_generated_errors": False,
    }

    cleaned_zipfile = clean.run(zipfile)

    assert (
        cleaned_zipfile["parsed"].values.tolist()
        == expected_cleaned_parsed.values.tolist()
    )

    assert (
        cleaned_zipfile["parsed_with_xml"].values.tolist()
        == expected_cleaned_parsed_with_xml.values.tolist()
    )
