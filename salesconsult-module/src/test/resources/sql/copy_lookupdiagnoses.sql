INSERT INTO consult_lookup_diagnose
(id,
 version,
 member_id,
 nomenclature,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on, deleted)
SELECT LOOKUPDIAGNOSIS_ID,
       VERSION,
       MID,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now(), 'N'
from vmas.lookupdiagnose
where mid = 77 or mid = -1;
# copy the -1 to 77, so per member they will have the full list
INSERT INTO consult_lookup_diagnose
(
 version,
 member_id,
 nomenclature,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on, deleted)
SELECT
       VERSION,
       77,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now(), 'N'
from consult_lookup_diagnose where member_id = -1
