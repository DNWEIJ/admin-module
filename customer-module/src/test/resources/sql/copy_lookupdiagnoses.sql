 INSERT INTO customer_lookup_diagnose
(id,
 version,
 member_id,
 nomenclature,
 venom_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPDIAGNOSIS_ID,
       VERSION,
       MID,
       NOMENCLATURE,
       VENOM_ID,
       'migration',
       now(),
       'migration',
       now()
from vmas.lookupdiagnose
where mid = 77