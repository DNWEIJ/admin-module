INSERT INTO supply_costing_batch_number
(id,
 version,
 costing_id,
 batch_number,
 member_id,
 local_member_id,
 start_date,
 end_date,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on
)

SELECT COSTINGBATCHNR_ID,
       VERSION,
       COSTING_ID,
       BATCH_NUMBER,
       MID,
       MLID,
       STARTDATE,
       ENDDATE,
       ADDEDBY,
       ADDEDON,
       LASTEDITEDBY,
       LASTEDITEDON
FROM vmas.costingbatchnumber
WHERE MID = 77
   or mid = -1;