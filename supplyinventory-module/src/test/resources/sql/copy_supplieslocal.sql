INSERT INTO new_vmas.SUPPLY_SUPPLYLOCAL
(id,
 version,
 local_member_id,
 member_id,
 buy_quantity,
 individual_quantity,
 min_quantity,
 quantity,
 supply_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT SUPPLIES2LOCAL_ID,
       VERSION,
       MLID,
       MID,
       BUYQUANTITY,
       INDIVIDUALQUANTITY,
       MINQUANTITY,
       QUANTITY,
       SUPPLIES2_ID,
       USERNAME,
       now(),
       'system',
       LASTALTERED
FROM vmas.supplies2local
WHERE MID = 77;