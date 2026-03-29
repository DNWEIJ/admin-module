update consult_estimate_lineitem l
    join  vmas.costing c ON  l.member_id =c.mid and l.category_id = c.lookupcostingcategory_id and l.nomenclature = c.nomenclature and c.prescriptionLabel is not null and c.prescriptionLabel <> ''
set has_print_label = 1, l.costing_id = c.costing_id;


update sales_line_item l
    join  vmas.costing c ON  l.member_id =c.mid and l.category_id = c.lookupcostingcategory_id and l.nomenclature = c.nomenclature and c.prescriptionLabel is not null and c.prescriptionLabel <> ''
set has_print_label = 1, l.costing_id = c.costing_id;



# Query OK, 0 rows affected (10 min 17.352 sec)
# Rows matched: 62991  Changed: 0  Warnings: 0