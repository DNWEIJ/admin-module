ALTER TABLE admin_member DROP CONSTRAINT SYS_CT_10119;
ALTER TABLE admin_member DROP CONSTRAINT SYS_CT_10120;
ALTER TABLE admin_member DROP CONSTRAINT SYS_CT_10131;
ALTER TABLE admin_member DROP CONSTRAINT SYS_CT_10132;
ALTER TABLE admin_member DROP CONSTRAINT SYS_CT_10133;
commit;

ALTER TABLE admin_member ALTER COLUMN START RENAME TO START_DATE;
ALTER TABLE admin_member ALTER COLUMN STOP RENAME TO STOP_DATE;
commit;

update admin_member set LOCAL_MEMBER_SELECT_REQUIRED = 'N';
update admin_member set ACTIVE = 'Y';
commit;

update admin_user set language = 'en';
update admin_user set login_enabled = 'Y';
update admin_user set PERSONNEL_STATUS = 'V';
commit;