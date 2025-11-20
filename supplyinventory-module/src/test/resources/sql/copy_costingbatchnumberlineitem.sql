INSERT INTO supply_costing_batch_number_lineitem
(id,
 version,
 costing_batch_nr_id,
 line_item_id,
 member_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on)
SELECT
COSTINGBATCHNRUSAGE_ID,
 VERSION,
 COSTINGBATCHNR_ID,
 LINEITEM_ID,
 77,
 ADDEDBY,
  ADDEDON,
 LASTEDITEDBY,
 LASTEDITEDON
FROM vmas.costingbatchnumberlineitem