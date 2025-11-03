 INSERT INTO customer_lookup_location

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
       77,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now()

FROM vmas.lookuplocation