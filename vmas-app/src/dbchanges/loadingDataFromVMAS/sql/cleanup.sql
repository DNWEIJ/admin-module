DELETE cpv
FROM new_vmas.consult_payment_visit cpv
         JOIN new_vmas.sales_payment sp
              ON cpv.payment_id = sp.id
WHERE sp.amount = 0.0;

delete FROM new_vmas.sales_payment where amount = 0.0;
