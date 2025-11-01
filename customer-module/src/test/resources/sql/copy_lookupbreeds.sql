INSERT INTO customer.customer_lookupbreeds
(id,
 version,
 member_id,
 local_member_id,
 lookupspecies_id,
 species_name,
 breed,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPBREED_ID,
       VERSION,
       MID,
       0,
       LOOKUPSPECIES_ID,
       SPECIES,
       BREED,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupbreed
where mid = -1
   OR 77;