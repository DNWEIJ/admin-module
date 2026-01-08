"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_appointment.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_payment.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_visit.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lineitem.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_payment_visit.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookuplocation.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookupdiagnoses.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookuproom.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_diagnose.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_estimate.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_estimate_for_pet.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_estimatelineitem.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_analyse.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_analyse_description.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_analyse_item.sql