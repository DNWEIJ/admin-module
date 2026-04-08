INSERT INTO supply_product_group
(id,
 version,
 member_id,
 parent_product_id,
 child_product_id,
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