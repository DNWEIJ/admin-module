INSERT INTO consult_analyse
(id,
 version,
 member_id,
 analyse_description_id,
 costing_id,
 lookup_costing_category_id,
 quantity,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT ANALYSE_ID,
       VERSION,
       MID,
       AnalyseDescription_ID,
       COSTING_ID,
       LOOKUPCOSTINGCATEGORY_ID,
       QUANTITY,
       ADDEDBY,
       ADDEDON,
       LASTEDITEDBY,
       LASTEDITEDON
FROM vmas.analyse
WHERE MID = 77;