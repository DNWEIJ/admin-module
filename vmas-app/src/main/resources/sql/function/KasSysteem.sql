--
-- Definition of procedure `VisitPaymentCorrect`
--
-- This will correct the link for visit payment
--
--   50    2010-01-18 00:00:00 
--   60    2010-02-15 00:00:00 
--  call KasSysteem(50, '2010-01-18 00:00', '2010-03-31 23:59');		 
--  call KasSysteem(60, '2010-02-15 00:00', '2010-03-31 23:59');
--
-- select sum(paymentAmount), sum(totalvisitAmountInc), sum(totalvisitAmountEx), sum(AppliedNulTax), sum(AppliedSixTax), sum(appliedNineteentax), sum(TaxSixTax), sum(TaxdNineTeenTax0), sum(TaxdNineTeenTax1) , sum(TaxdNineTeenTax2) from TMP_VisitRecords;

DELIMITER $$

DROP PROCEDURE IF EXISTS `KasSysteem` $$
CREATE  PROCEDURE `KasSysteem`(IN IN_MID bigint(20), IN IN_FROMDATE datetime, IN IN_TODATE datetime)
BEGIN

	/* Setup Tempory Teables */
	DROP TEMPORARY table IF EXISTS TMP_VisitRecords;
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_VisitRecords (
	    Payment_Id bigint(20),
	    Visit_ID  bigint(20),
	    Appointment_id bigint(20),
	    patient_id  bigint(20),
	    paymentdate datetime,
	    PaymentAmount double DEFAULT-1,
		TotalVisitAmountInc double DEFAULT -1,
		TotalVisitAmountEx  double DEFAULT -1,
		AppliedNulTax double DEFAULT 0.0,
	    AppliedSixTax double DEFAULT 0.0,
	    AppliedNineTeenTax double DEFAULT 0.0, 
	    AppliedNineTeenTax0  double DEFAULT 0.0, 
	    AppliedNineTeenTax1  double DEFAULT 0.0, 
	    TaxSixTax double DEFAULT 0.0,
	    TaxdNineTeenTax0 double DEFAULT 0.0,
	    TaxdNineTeenTax1 double DEFAULT 0.0,
	    TaxdNineTeenTax2 double DEFAULT 0.0,
	    mid bigint(20)
	);
	


	INSERT INTO TMP_VisitRecords
		SELECT
		    p.payment_id,
		    pv.Visit_ID,
		    -1,
		    -1,
		    p.paymentdate,
		    sum(p.Amount),
		    -1,
			-1,
			-1,
			-1,
		    -1, 			
		    -1, 
		    -1, 
		    -1, 
		    -1,
		     -1, 
		    -1,
		    p.mid
		FROM paymentvisit pv Left Join payment p ON pv.payment_id = p.payment_id
		WHERE p.paymentdate > IN_FROMDATE and p.paymentdate <= IN_TODATE and p.mid = IN_MID group by pv.visit_id;

		-- update the appointment_id
		UPDATE  TMP_VisitRecords as vt
		SET Appointment_id = 
			(SELECT appointment_id FROM visit v WHERE v.visit_id = vt.visit_id);
	
		-- update the appointment_id
		UPDATE  TMP_VisitRecords as vt
		SET patient_id = 
		(SELECT patient_id FROM visit v WHERE v.visit_id = vt.visit_id);
		
		-- update the TotalVisitAmountInc
		UPDATE  TMP_VisitRecords as vt
		SET TotalVisitAmountInc = 
			(SELECT sum(total) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id);

		 -- update the TotalVisitAmountEx
		UPDATE  TMP_VisitRecords as vt
		SET TotalVisitAmountEx = 
			(SELECT sum(cost*quantity + processingfee) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id);

		-- update the AppliedNulTax
		UPDATE  TMP_VisitRecords as vt
		SET AppliedNulTax = 
			(SELECT sum(cost*quantity) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id AND tax = 0);
		
		 -- update the AppliedSixTax
		UPDATE  TMP_VisitRecords as vt
		SET AppliedSixTax = 
			(SELECT sum(cost*quantity ) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id AND tax = 1) ;
		
		-- update the AppliedNineTeenTax
		UPDATE  TMP_VisitRecords as vt
		SET AppliedNineTeenTax = 
			(SELECT sum(cost*quantity + processingfee) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 2);

		UPDATE  TMP_VisitRecords as vt
		SET AppliedNineTeenTax0 = 
			(SELECT sum(processingfee) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 0);
		
		UPDATE  TMP_VisitRecords as vt
		SET AppliedNineTeenTax1 = 
			(SELECT sum(processingfee) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 1);
					
			
		 -- update the TaxSixTax
		UPDATE  TMP_VisitRecords as vt
		SET TaxSixTax = 
			(SELECT sum(cost*quantity * 0.06 ) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id AND tax = 1) ;
		
		-- update the TaxdNineTeenTax
		UPDATE  TMP_VisitRecords as vt
		SET TaxdNineTeenTax2 = 
			(SELECT sum( (cost*quantity + processingfee) * 0.19 ) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 2);
			
		UPDATE  TMP_VisitRecords as vt
		SET TaxdNineTeenTax0 = 
			(SELECT sum(PROCESSINGFEESERVICETAXPORTION ) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 0);
			
		UPDATE  TMP_VisitRecords as vt
		SET TaxdNineTeenTax1 = 
			(SELECT sum( PROCESSINGFEESERVICETAXPORTION) FROM lineitem l WHERE l.appointment_id = vt.appointment_id and l.patient_id = vt.patient_id  AND tax = 1);
		
	 
END $$

DELIMITER ;