INSERT INTO salesconsult.consult_appointment
(id,
 version,
 member_id,
 local_member_id,
 visit_date_time,
 cancelled,
 completed,
 picked_up,
 otc,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)

SELECT APPOINTMENT_ID,
       VERSION,
       MID,
       MLID,
       VISITDATE,
       CANCELLED,
       COMPLETED,
       PICKEDUP,
       OTC,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.appointment
where mid = 77;