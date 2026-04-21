INSERT INTO new_vmas.SUPPLY_SUPPLYLOCAL
(id,
 version,
 local_member_id,
 member_id,
 quantity_of_packages,
 individual_left_in_open_package,
 min_quantity_for_alert,
 buy_quantity_per_order,
 supply_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT SUPPLIES2LOCAL_ID,
       VERSION,
       MLID,
       MID,
       QUANTITY,
       INDIVIDUALQUANTITY,
       MINQUANTITY,
       BUYQUANTITY,
       SUPPLIES2_ID,
       USERNAME,
       now(),
       'system',
       LASTALTERED
FROM vmas.supplies2local
WHERE MID = 77;