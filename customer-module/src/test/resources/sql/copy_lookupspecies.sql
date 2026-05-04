INSERT INTO customer_lookup_species
(id,
 version,
 member_id,
 specy,
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
where mid = -1
   OR mid = 77;

INSERT INTO new_vmas.customer_lookup_species
(added_by,
 added_on,
 last_edited_by,
 last_edited_on,
 version,
 member_id,
 specy)
SELECT added_by,
       added_on,
       last_edited_by,
       last_edited_on,
       version,
       77,
       specy
FROM new_vmas.customer_lookup_species
WHERE member_id = -1;