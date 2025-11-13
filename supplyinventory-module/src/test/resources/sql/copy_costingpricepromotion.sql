INSERT INTO supply_costing_price_promotion
(id,
  version,
 costing_id,
 start_date,end_date,
 cost,
 processing_fee,
 total_amount,
  reduction_percentage,
 member_id,
 added_by,
 added_on,
 last_edited_by,
 last_edited_on
)
SELECT
COSTINGSPECIALPRICE_ID,
 VERSION,
 Costing_ID,
 STARTDATE,
 ENDDATE,
 COST,
 PROCESSINGFEE,
 AMOUNT,
 REDUCTION,
 MID,
 ADDEDBY,
 ADDEDON,
 LASTEDITEDBY,
 LASTEDITEDON
FROM vmas.costingspecialprice
where mid = 77