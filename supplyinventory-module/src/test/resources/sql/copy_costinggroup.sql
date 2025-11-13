INSERT INTO supply_costing_group
(id,
 version,
 member_id,
 parent_costing_id,
 child_costing_id,
 quantity,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT COSTINGGROUP_ID,
       VERSION,
       MID,
       PARENTCOSTING_ID,
       CHILDCOSTING_ID,
       QUANTITY,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.costinggroup
WHERE MID = 77
   or mid = -1;