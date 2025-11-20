SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';
INSERT INTO supply_lookup_costing_category
(id,
 version,
 member_id,
 category,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPCOSTINGCATEGORY_ID,
       VERSION,
       MID,
       CATEGORY,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupcostingcategory
WHERE MID = 77
   or mid = -1;
#
# INSERT INTO supplyinventory_lookup_costing_category
# (id,
#  version,
#  member_id,
#  category,
#  added_by,
#  added_on,
#  last_edited_by,
#  last_edited_on)
# VALUES (0,
#         0,
#         -1,
#         'unkown',
#         'migration',
#         now(),
#         'migration',
#         now());

# update supply_costing set lookupcostingcategory_id = (select max(id) from vmas.lookupcostingcategory) where lookupcostingcategory_id = 0