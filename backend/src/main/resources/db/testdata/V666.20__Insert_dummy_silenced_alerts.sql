INSERT INTO silenced_alerts (vessel_name, internal_reference_number, external_reference_number, ircs, vessel_identifier, silenced_before_date, silenced_after_date, value) VALUES
('NATUREL NON FUIR', 'ABC000571489', 'IS726385', 'LRED', 'INTERNAL_REFERENCE_NUMBER', NOW() + ('15 DAYS')::interval, NOW() - ('1 DAY')::interval, ('{' ||
    '"seaFront": "MEMN",' ||
    '"flagState": "FR",' ||
    '"riskFactor": 3.6947,' ||
    '"type": "TWELVE_MILES_FISHING_ALERT",' ||
    '"natinfCode": "27689"' ||
    '}')::jsonb),
('JARDIN TANT DESCENDRE', 'ABC000417080', 'KS181242', 'QFH9332', 'INTERNAL_REFERENCE_NUMBER', NOW() + ('50 DAYS')::interval, null, ('{' ||
    '"seaFront": "NAMO",' ||
    '"flagState": "FR",' ||
    '"riskFactor": 3.6947,' ||
    '"type": "MISSING_FAR_ALERT",' ||
    '"natinfCode": "27689"' ||
    '}')::jsonb),
('FRAPPER PREFERER RIRE', 'ABC000900977', 'QG773364', 'CA2048', 'INTERNAL_REFERENCE_NUMBER', NOW() + ('5 HOURS')::interval, null, ('{' ||
    '"seaFront": "SA",' ||
    '"flagState": "FR",' ||
    '"riskFactor": 3.6947,' ||
    '"type": "THREE_MILES_TRAWLING_ALERT",' ||
    '"natinfCode": "27689"' ||
    '}')::jsonb),
('FORTUNE ARME ABATTRE', 'ABC000677933', 'IG415546', 'UTIG', 'INTERNAL_REFERENCE_NUMBER', NOW() + ('24 HOURS')::interval, null, ('{' ||
    '"seaFront": "NAMO",' ||
    '"flagState": "FR",' ||
    '"riskFactor": 3.6947,' ||
    '"type": "THREE_MILES_TRAWLING_ALERT",' ||
    '"natinfCode": "27689"' ||
    '}')::jsonb);
