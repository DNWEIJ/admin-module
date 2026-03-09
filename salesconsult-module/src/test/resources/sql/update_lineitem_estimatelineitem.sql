update consult_estimate_lineitem l
    join  vmas.costing c ON  l.member_id =c.mid and l.category_id = c.lookupcostingcategory_id and l.nomenclature = c.nomenclature
set has_print_label = 1, l.costing_id = c.costing_id
WHERE  c.prescriptionLabel is not null and c.prescriptionLabel <> '';

update sales_line_item l
    join  vmas.costing c ON  l.member_id =c.mid and l.category_id = c.lookupcostingcategory_id and l.nomenclature = c.nomenclature
set has_print_label = 1, l.costing_id = c.costing_id
WHERE c.prescriptionLabel is not null and c.prescriptionLabel <> '';
