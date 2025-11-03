 INSERT INTO customer_lookup_purpose
(id,
 version,
 member_id,
 defined_purpose,
 time_in_minutes,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPPREDEFINEDPURPOSE_ID,
       VERSION,
       MID,
       LOOKUPPREDEFINEDPURPOSE,
       TYPICALTIME,
           'migration',
       now(),
       'migration',
       now()
FROM vmas.lookuppredefinedpurpose
where mid = -1 OR mid=77;