SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

INSERT INTO supply_lookup_product_category
(id,
 version,
 member_id,
 category_name,
 deleted,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPCOSTINGCATEGORY_ID,
       VERSION,
       MID,
       CATEGORY,
       'N',
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupcostingcategory
WHERE MID = 77 or mid = -1;

update new_vmas.supply_lookup_product_category set member_id = 77;