INSERT INTO consult_diagnose
(id,
 version,
 member_id,
 patient_id,
 appointment_id,
 lookup_diagnose_id,
 lookup_location_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT d.DIAGNOSE_ID,
       d.VERSION,
       d.MID,
       d.PATIENT_ID,
       d.APPOINTMENT_ID,
       d.LOOKUPDIAGNOSIS_ID,
       l.LOOKUPLOCATION_ID,
       d.ADDEDBY,
       d.ADDEDON,
       'system',
       now()
from vmas.diagnose d
         join vmas.location l on l.DIAGNOSE_ID = d.DIAGNOSE_ID
where d.mid = 77