rem drop schema; create schema
rem C:\workspace\admin\vmas-app\src\test\resources\drop_schema_vmas.sql
rem C:\workspace\admin\vmas-app\src\test\resources\create_schema_vmas.sql
rem
rem run app to create tables
rem now run below
rem
cd C:\workspace\admin\customer-module\src\test\resources\sql
call .\run_insert_scripts.bat
cd C:\workspace\admin\salesconsult-module\src\test\resources\sql
call .\run_insert_scripts.bat
cd C:\workspace\admin\supplyinventory-module\src\test\resources\sql
call .\run_insert_scripts.bat
cd C:\workspace\admin\vmas-app\src\test\resources