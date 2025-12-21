INSERT INTO consult_lookup_diagnose
(id,
 version,
 member_id,
 nomenclature,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPDIAGNOSIS_ID,
       VERSION,
       MID,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now()
from vmas.lookupdiagnose
where mid = 77 or mid = -1