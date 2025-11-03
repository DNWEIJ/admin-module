 INSERT INTO customer_lookup_room
(id,
 version,
 member_id,
 local_member_id,
 room,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPPREDEFINEDROOM_ID,
       VERSION,
       MID,
       MLID,
       ROOM,
           'migration',
       now(),
       'migration',
       now()
from vmas.lookuppredefinedroom
where mid = 77