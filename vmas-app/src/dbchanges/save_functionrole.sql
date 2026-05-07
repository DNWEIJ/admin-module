SELECT CONCAT(
               'INSERT INTO new_vmas.admin_function_role (added_on, function_id, last_edited_on, member_id, role_id, version, added_by, last_edited_by) ',
               'VALUES (''2026-05-07 10:10:44'', ', function_id, ', ''2026-05-07 10:10:44'', 77, ', role_id, ', 0, ''system'', ''system'');'
       )
INTO OUTFILE 'C:/workspace/admin/vmas-app/src/dbchanges/loadingDataFromVMAS/sql/add_funtionrole.sql'
FROM new_vmas.admin_function_role;