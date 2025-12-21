INSERT INTO consult_analyse_description
(id,
 version,
 member_id,
 description,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT ANALYSEDESCRIPTION_ID,
       VERSION,
       MID,
       DESCRIPTION,
       ADDEDBY,
       ADDEDON,
       LASTEDITEDBY,
       LASTEDITEDON
FROM vmas.analysedescription
where mid = 77;