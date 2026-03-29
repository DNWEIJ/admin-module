INSERT INTO sales_refund
(id,
 version,
 customer_id,
 member_id,
 local_member_id,
 refund_date,
 amount,
 comments,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT REFUND_ID,
       VERSION,
       OWNER_ID,
       MID,
       MLID,
       REFUNDDATE,
       AMOUNT,
       COMMENTS,
       'system',
       now(),
       'system',
       now()
FROM vmas.refund
WHERE mid = 77;