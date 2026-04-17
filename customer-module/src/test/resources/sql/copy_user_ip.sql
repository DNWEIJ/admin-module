INSERT INTO admin_user_preferences
(id,
 version,
 member_id,
 user_id,
 ipnumber,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT IPSECURITY_ID,
       VERSION,
       MEMBER_ID,
       USER_ID,
       IP_NUMBER,
       'system',
       now(),
       'system',
       now()
FROM vmas.thau_ipsecurity
WHERE MEMBER_ID = 77;
