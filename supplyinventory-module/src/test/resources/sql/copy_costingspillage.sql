INSERT INTO supply_costing_spillage
(id,
 version,
 costing_id,
 member_id,
 local_member_id,
 package_amount,
 start_date,
 end_date,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT COSTINGSPILLAGE_ID,
       VERSION,
       COSTING_ID,
       MID,
       MLID,
       PACKAGEAMOUNT,
       STARTDATE,
       ENDDATE,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.costingspillage
WHERE MID = 77