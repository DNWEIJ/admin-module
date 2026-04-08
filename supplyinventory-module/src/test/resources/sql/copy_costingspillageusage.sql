INSERT INTO supply_product_spillage_usage
(id,
 version,
 product_spillage_id,
 line_item_id,
 member_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT COSTINGSPILLAGEUSAGE_ID,
       VERSION,
       COSTINGSPILLAGE_ID,
       LINITEM_ID,
       77,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.costingspillageusage