rem
set start=%TIME%

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot < .\sql\drop_schema_new_vmas.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot  < .\sql\create_schema_new_vmas.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot  new_vmas < C:\workspace\admin\vmas-app\src\dbchanges\v1\create_new-vmas_tables.sql

cd C:\workspace\admin\customer-module\src\test\resources\sql
call .\run_insert_scripts.bat

cd C:\workspace\admin\supplyinventory-module\src\test\resources\sql
call .\run_insert_scripts.bat

cd C:\workspace\admin\salesconsult-module\src\test\resources\sql
call .\run_insert_scripts.bat

cd  C:\workspace\admin\vmas-app\src\dbchanges\loadingDataFromVMAS
call .\run_additional_scripts.bat

cd C:\workspace\admin\vmas-app\src\test\resources
echo Start: %start%
echo End: %TIME%
