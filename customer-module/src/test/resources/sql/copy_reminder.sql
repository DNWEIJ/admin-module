 INSERT INTO customer_reminder
(id,
  version,
 pet_id,
  member_id,
 due_date,
 reminder_text,
 originating_appointment_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT
REMINDER_ID,
    VERSION,
    PATIENT_ID,
    MID,
    DUEDATE,
    REMINDER,
    ORIGINATIONAPPOINTMENT_ID,
    ADDEDBY,
    ADDEDON,
    LASTEDITEDBY,
    LASTEDITEDON
FROM vmas.reminder
    WHERE mid = 77;