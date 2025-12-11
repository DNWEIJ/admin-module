INSERT INTO admin_user_preferences (
    ID,
    VERSION,
    MEMBER_ID,
    ADDED_BY,
    ADDED_ON,
    LAST_EDITED_BY,
    LAST_EDITED_ON,
    preferences_json
)
SELECT USER_ID,
       VERSION,
       MEMBER_ID,
       ADDED_BY,
       ADDED_ON,
       LAST_EDITED_BY,
       LAST_EDITED_ON,
       JSON_OBJECT(
               'agendaVet1', PREF_AGENDA_VET1,
               'agendaVet2', PREF_AGENDA_VET2,
               'agendaVet3', PREF_AGENDA_VET3,
               'searchCustStart', PREF_SEARCH_CUST_START,
               'searchCustStreet', PREF_SEARCH_CUST_STREET,
               'visitAppointmentList', PREF_VISIT_APPOINTMENT_LIST,
               'visitTotalVisit', PREF_VISIT_TOTAL_VISIT,
               'visitAppointmentInfo', PREF_VISIT_APPOINTMENT_INFO,
               'visitVisitInfo', PREF_VISIT_VISIT_INFO,
               'visitAnalyseInfo', PREF_VISIT_ANALYSE_INFO,
               'visitProducts', PREF_VISIT_PRODUCTS,
               'visitDiagnoses', PREF_VISIT_DIAGNOSES,
               'visitComments', PREF_VISIT_COMMENTS,
               'visitImages', PREF_VISIT_IMAGES
       ) AS user_preferences_json
FROM
    vmas.thau_user WHERE MEMBER_ID = 77;

UPDATE admin_user set meta_user_preferences_id = id;