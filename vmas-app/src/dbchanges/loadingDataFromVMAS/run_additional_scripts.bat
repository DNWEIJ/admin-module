"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\sql\cleanup.sql


"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\sql\add_role.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\sql\add_function.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\sql\add_userrole.sql
