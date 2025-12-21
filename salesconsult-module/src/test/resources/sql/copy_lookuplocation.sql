 INSERT INTO consult_lookup_location
(id,
 version,
 member_id,
 nomenclature,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
select LOOKUPLOCATION_ID,
       VERSION,
       -1,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now()
FROM vmas.lookuplocation