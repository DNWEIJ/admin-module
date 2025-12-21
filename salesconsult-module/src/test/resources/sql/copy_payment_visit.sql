INSERT INTO consult_payment_visit
(id,
 version,
 member_id,
 payment_id,
 visit_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on
)
SELECT
PAYMENTVISIT_ID,
    VERSION,
    MID,
    PAYMENT_ID,
    VISIT_ID,
    ADDEDBY,
    ADDEDON,
    LASTEDITEDBY,
    LASTEDITEDON
FROM vmas.paymentvisit
where mid = 77;