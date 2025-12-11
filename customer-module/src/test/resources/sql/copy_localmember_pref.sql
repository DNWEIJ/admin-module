INSERT INTO ADMIN_LOCALMEMBER_PREFERENCES (
    ID,
    VERSION,
    MEMBER_ID,
    ADDED_BY,
    ADDED_ON,
    LAST_EDITED_BY,
    LAST_EDITED_ON,
    preferences_json
)
SELECT
    MEMBERLOCAL_ID,
    VERSION,
    MEMBER_ID,
    ADDED_BY,
    ADDED_ON,
    LAST_EDITED_BY,
    LAST_EDITED_ON,
    JSON_OBJECT(
            'estimatedTime', PREF_ESTIMATED_TIME,
            'insuranceCompany', PREF_INSURANCE_COMPANY,
            'paymentMethod', PREF_PAYMENT_METHOD,
            'firstPageMessage', FIRST_PAGE_MESSAGE,
            'room1', PREF_ROOM1,
            'room2', PREF_ROOM2,
            'room3', PREF_ROOM3,
            'room4', PREF_ROOM4,
            'roomAgenda', PREF_ROOM_AGENDA,
            'rxlabel', pref_rxlabel,
            'mandatoryReason', MANDATORY_REASON,
            'consultTextTemplate', CONSULT_TEXT_TEMPLATE,
            'openingstimes', OPENINGSTIMES,
            'sendoutAppointmentReminderMail', SENDOUT_APPOINTMENT_REMINDER_MAIL
    ) AS preferences_json
FROM vmas.thau_memberlocal where MEMBER_ID = 77;

# update the id in memberlocal
UPDATE admin_localmember  set   meta_local_member_preferences_id = id;