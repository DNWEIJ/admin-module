INSERT INTO consult_estimate_for_pet
(id,
 version,
 member_id,
 pet_id,
 estimate_id,
 purpose,
 comments,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT ESTIMATESPECIFIC_ID,
       VERSION,
       MID,
       PATIENT_ID,
       ESTIMATE_ID,
        PURPOSE,
       COMMENTS,
       ADDEDBY,
       ADDEDON,
       LASTEDITEDBY,
       LASTEDITEDON
FROM vmas.estimatespecific where mid = 77