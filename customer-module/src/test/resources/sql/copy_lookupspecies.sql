 INSERT INTO customer_lookup_species
(id,
 version,
 member_id,
 species,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT LOOKUPSPECIES_ID,
       VERSION,
       MID,
       SPECIES,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupspecies
where mid = -1 OR mid=77;