"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_member.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_localmember.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_localmember_tax.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_localmember_pref.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_user.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_user_pref.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_customer.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_pet.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookupspecies.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookupbreeds.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookupnotepurpose.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_lookuppurpose.sql

"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_note.sql
"C:\Program Files\MariaDB 10.5\bin\mysql.exe" -u root -proot new_vmas < .\copy_reminder.sql