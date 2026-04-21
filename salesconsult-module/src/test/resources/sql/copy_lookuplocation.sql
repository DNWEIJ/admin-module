 INSERT INTO consult_lookup_location
(id,
 version,
 member_id,
 nomenclature,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on,
 deleted)
select LOOKUPLOCATION_ID,
       VERSION,
       -1,
       NOMENCLATURE,
       'migration',
       now(),
       'migration',
       now(),
       'N'
FROM vmas.lookuplocation;

 INSERT INTO consult_lookup_location
 (
  version,
  member_id,
  nomenclature,
  added_by,
  added_on,
  last_edited_by,
  last_edited_on,
  deleted)
 select
        VERSION,
        77,
        NOMENCLATURE,
        'migration',
        now(),
        'migration',
        now(),
        'N'
 FROM consult_lookup_location where member_id = -1