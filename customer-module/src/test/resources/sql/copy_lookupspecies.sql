INSERT INTO customer.customer_lookupspecies
(id,
 version,
 member_id,
 local_member_id,
 species,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPSPECIES_ID,
       VERSION,
       MID,
       0,
       SPECIES,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupspecies
where mid = -1
   OR 77;