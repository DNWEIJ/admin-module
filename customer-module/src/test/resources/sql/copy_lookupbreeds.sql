 INSERT INTO customer_lookup_breeds
(id,
 version,
 member_id,
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
        LOOKUPSPECIES_ID,
       SPECIES,
       BREED,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookupbreed
where mid = -1 OR mid=77;

 INSERT INTO new_vmas.customer_lookup_breeds
 (added_by,
  added_on,
  last_edited_by,
  last_edited_on,
  version,
  member_id,
  breed,
  species_name,
  lookupspecies_id)
 SELECT
     b.added_by,
     b.added_on,
     b.last_edited_by,
     b.last_edited_on,
     b.version,
     77,
     b.breed,
     b.species_name,
     new_species.id
 FROM new_vmas.customer_lookup_breeds b
          JOIN customer_lookup_species old_species ON b.lookupspecies_id = old_species.id AND old_species.member_id = -1
          JOIN customer_lookup_species new_species ON old_species.species = new_species.species AND new_species.member_id = 77
 WHERE b.member_id = -1;