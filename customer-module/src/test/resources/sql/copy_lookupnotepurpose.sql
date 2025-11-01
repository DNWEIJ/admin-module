INSERT INTO customer.customer_lookup_notepurpose
(id,
 version,
 member_id,
 pre_defined_purpose,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on
)
SELECT
LOOKUPPREDEFINEDNOTEPADPURPOSE_ID,
    VERSION,
    MID,
    PREDEFINEDPURPOSE,
    'migration',
    now(),
'migration',
now()
FROM vmas.lookuppredefinednotepadpurpose
where mid = -1
   OR 77;