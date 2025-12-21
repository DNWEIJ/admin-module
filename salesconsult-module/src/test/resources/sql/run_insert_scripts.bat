"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_appointment.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_payment.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_visit.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lineitem.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_payment_visit.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookuplocation.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookupdiagnoses.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_diagnose.sql