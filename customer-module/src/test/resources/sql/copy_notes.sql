INSERT INTO customer.customer_notes
(id,
 version,
 local_member_id,
 member_id,
 note_date,
 note,
 purpose,
 staff_member,
 pet_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT NOTEPAD_ID,
       VERSION,
       0,
       MID,
       NOTEDATE,
       IfNull(NOTES, ''),
       PURPOSE,
       STAFFMEMBER,
       PATIENT_ID,
       ADDEDBY,
       ADDEDON,
       LASTEDITEDBY,
       LASTEDITEDON
FROM vmas.notepad
where mid = 77;