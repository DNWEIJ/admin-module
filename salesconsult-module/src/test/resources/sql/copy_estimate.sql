INSERT INTO consult_estimate
(id,
 version,
 member_id,
 local_member_id,
 estimate_date,
 trans_to_visit,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT ESTIMATE_ID,
       VERSION,
       MID,
       MLID,
       ESTIMATEDATE,
       TRANSTOVISIT,
       'system',
       now(),
       'system',
       now()
from vmas.estimate
where mid = 77