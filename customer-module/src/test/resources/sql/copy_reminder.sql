INSERT INTO customer.customer_reminder
(id,
  version,
 pet_id,
 local_member_id,
 member_id,
 due_date,
 reminder,
 originating_appointment_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT
REMINDER_ID,
    VERSION,
    PATIENT_ID,
    0,
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