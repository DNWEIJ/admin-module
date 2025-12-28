INSERT INTO consult_diagnose
(id,
 version,
 member_id,
 pet_id,
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
where l.diagnose_id not in(
    # filter the diagnoses having multiple location, need to add them as separate records
    SELECT diagnose_id FROM vmas.location l where l.mid = 77 GROUP BY diagnose_id HAVING COUNT(*) > 1
) and d.mid = 77 and l.mid = 77 and d.diagnose_id not in (511);

# INSERT THE MULTIPLE REFERENCED INTO CONSULT_DIAGNOSE AS SEPERATE ROWS
INSERT INTO consult_diagnose
(
 version,
 member_id,
 pet_id,
 appointment_id,
 lookup_diagnose_id,
 lookup_location_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT
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
where l.diagnose_id in(
    # filter the diagnoses having multiple location, need to add them as separate records
    SELECT diagnose_id FROM vmas.location l where l.mid = 77 GROUP BY diagnose_id HAVING COUNT(*) > 1
) and d.mid = 77 and l.mid = 77 and d.diagnose_id not in (511);