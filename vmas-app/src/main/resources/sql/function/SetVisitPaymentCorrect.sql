--
-- Definition of procedure `VisitPaymentCorrect`
--
-- This will correct the link for visit payment
--
--   50    2010-01-18 00:00:00 
--   60    2010-02-15 00:00:00 
--  call VisitPaymentCorrect(50, '2010-01-18 00:00', '2010-05-01 00:00');		 
--  call VisitPaymentCorrect(60, '2010-02-15 00:00', '2010-05-01 00:00');		 
DELIMITER $$

DROP PROCEDURE IF EXISTS `VisitPaymentCorrectSetUp` $$
CREATE  PROCEDURE `VisitPaymentCorrectSetUp`(IN IN_MID bigint(20), IN IN_FROMDATE datetime, IN IN_TODATE datetime)
BEGIN

	/* Setup Tempory Teables */
	DROP TEMPORARY table IF EXISTS TMP_PaymentConnectVisit;
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_PaymentConnectVisit (
	    Payment_Id bigint(20),
	    Customer_Id  bigint(20),
	    Visit_ID  bigint(20),
	    PaymentDate  datetime,
	    OwnerName varchar (55) DEFAULT'-- Misc Transaction --',
	    PaymentMethod int,
	    PaymentAmount double DEFAULT-1,
		VisitAmount double DEFAULT -1,
		PaymentTotalAmount double DEFAULT -1,
	    Applied double DEFAULT 0.0,
	    AppliedGoodTax double DEFAULT 0.0,
	    AppliedServiceTax double DEFAULT 0.0,
	    Balance double DEFAULT -1,
	     mid bigint(20)
	);
	
	DROP TEMPORARY table IF EXISTS TMP_VisitTotal;
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_VisitTotal (
	    Customer_Id  bigint(20),
	    OwnerName varchar (55) DEFAULT'-- Misc Transaction --',
	    Patient_Id  bigint(20),
	    Visit_ID  bigint(20),
	    Payment_id  bigint(20),
	    Appointment_ID  bigint(20),
	    VisitDate  datetime,
	    VisitAmount double DEFAULT-1, 
	    VisitDayAmount double DEFAULT-1,
	    VisitTotalAmount double DEFAULT-1,
	     mid bigint(20)
	);
	
	DROP TEMPORARY table IF EXISTS TMP_TMP_VisitTotal;
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_TMP_VisitTotal (
	    Customer_Id  bigint(20),
	    Visit_ID  bigint(20),
	    VisitDate  datetime,
	    VisitAmount double DEFAULT-1,
	    mid bigint(20)
	);
	
	/* Load all payments that do not have a connection */
	INSERT INTO TMP_PaymentConnectVisit
		SELECT  
		    pay.Payment_ID,
		    pay.Customer_id,
		    pv.Visit_ID,
		    pay.PaymentDate,
		    FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial) AS OwnerName,
		    pay.Method,
		    pay.Amount,
		    -1,
			-1,
			-1,
		    -1,
			-1,
		    -1,
		    pay.mid
		FROM payment pay LEFT JOIN paymentvisit pv ON pay.payment_id = pv.payment_id
		 INNER JOIN customer c ON c.customer_id = pay.customer_Id
		WHERE pay.paymentdate > IN_FROMDATE and pay.paymentdate <= IN_TODATE and pay.mid = IN_MID
--      WHERE pay.paymentdate > '2010-01-17 00:00' and pay.paymentdate <= '2010-05-01 23:59' and pay.mid = 50
		and pv.visit_id is null;		 


	  /* update data with the balance information */
	UPDATE  TMP_PaymentConnectVisit as PCB
			SET Balance =
			  round( ( COALESCE((select Sum(Amount) FROM payment p WHERE p.customer_id = PCB.customer_Id),0) -
			     COALESCE((select Sum(Total) FROM LineItem LI INNER JOIN Patient pat ON LI.Patient_ID = pat.Patient_ID WHERE pat.customer_id = PCB.customer_Id),0)), 2);

	/* load all visits that do not have a connection to a paymentvisit record   */
	INSERT INTO TMP_VisitTotal
		SELECT
		-1,
		'',
		v.Patient_id ,
	    v.Visit_ID,
        pv.payment_id,
	    v.Appointment_ID,
	    a.visitdate,
	     ROUND(sum(l.total), 2),
	     0,
	     0,
	     v.mid
		FROM visit v 	LEFT JOIN paymentvisit pv ON v.visit_id = pv.visit_id
						INNER JOIN lineitem l ON v.patient_id = l.patient_id and v.appointment_id = l.appointment_id
						INNER JOIN appointment a ON v.appointment_id = a.appointment_id
		WHERE a.visitdate > IN_FROMDATE AND a.visitdate <= IN_TODATE  and a.mid = IN_MID
--		WHERE a.visitdate > '2010-01-17 00:00' AND a.visitdate <= '2010-05-01 23:59'  and a.mid = 50
		and pv.visit_id is null group by v.Patient_id , v.Visit_ID, v.Appointment_ID;
			  
-- update the customer_id in the tmp_visittotal table 
			UPDATE  TMP_VisitTotal as vt
			SET customer_id =
			 (SELECT customer_id FROM patient p  WHERE p.patient_id = vt.patient_id);
			UPDATE  TMP_VisitTotal as vt
			SET OwnerName =
			 (SELECT FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial) AS OwnerName FROM customer c  WHERE c.customer_id = vt.customer_id);
		-- delete payment zeor
		DELETE FROM TMP_PaymentConnectVisit WHERE PaymentAmount = 0.0;	 
-- Select all payment records having having the same amount/date/customer  on the visit
--	select p.payment_id, p.customer_id, v.customer_id, v.visit_id, p.paymentdate, v.visitdate, p.paymentamount, v.visitamount, v.appointment_id
--	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
--	where p.paymentamount = v.visitamount and DATE(p.paymentdate) = DATE(v.visitdate);

END $$




DROP PROCEDURE IF EXISTS `VisitPaymentCorrect` $$
CREATE  PROCEDURE `VisitPaymentCorrect`(IN IN_MID bigint(20),  IN IN_FROMDATE datetime, IN IN_TODATE datetime)
BEGIN
	-- setup the inforamtion	 
	 call VisitPaymentCorrectSetUp(IN_MID, IN_FROMDATE, IN_TODATE);		 

	 -- FIRST --	
	-- Make a connection if there is just 5 cent of difference
	INSERT INTO paymentvisit (version, mid, payment_id, visit_id, addedby, addedon, lasteditedby, lasteditedon)
	SELECT
  			0,
  			v.mid,
  		    p.payment_id,
            v.visit_id,
		   'Migration',
  			'2010-01-19 00:00',
	  	   'Migration',
  			'2010-01-19 00:00'
	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
	where(ABS(ROUND((p.paymentamount - v.visitamount),2)) < 0.05) and DATE(p.paymentdate) = DATE(v.visitdate);

	
	--  SECOND --
	-- Make a connection if a payment coffers more then one visit for same day
	-- first copy the records
	INSERT INTO TMP_TMP_VisitTotal  select Customer_Id, Visit_ID, VisitDate, visitamount, mid from TMP_VisitTotal ;
	-- update the visit same day total
	UPDATE  TMP_VisitTotal as vt
		SET VisitDayAmount =
		 (select round(sum(tv.VisitAmount),2) from TMP_TMP_VisitTotal tv where tv.customer_id = vt.customer_id AND DATE(tv.visitdate) = DATE(vt.visitdate)
     group by tv.customer_id, DATE(tv.visitdate) );
     
    INSERT INTO paymentvisit (version, mid, payment_id, visit_id, addedby, addedon, lasteditedby, lasteditedon)
	SELECT
  			0,
  			v.mid,
  		    p.payment_id,
            v.visit_id,
		   'Migration',
  			'2010-01-19 00:00',
	  	   'Migration',
  			'2010-01-19 00:00'
	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
	where(ABS(ROUND((p.paymentamount - v.VisitDayAmount),2)) < 0.05) and DATE(p.paymentdate) = DATE(v.visitdate);
	
	
	-- before we go further, reload the data, so all made connections will not be available as todo data anymore.
	 CALL VisitPaymentCorrectSetUp(IN_MID, IN_FROMDATE, IN_TODATE);
	 
	 
	-- THIRD -- 
	-- Now add records where the amount / customer is the same but a different date. 
    INSERT INTO paymentvisit (version, mid, payment_id, visit_id, addedby, addedon, lasteditedby, lasteditedon)
	SELECT
  			0,
  			v.mid,
  		    p.payment_id,
            v.visit_id,
		   'Migration',
  			'2010-01-19 00:00',
	  	   'Migration',
  			'2010-01-19 00:00'
	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
	where(ABS(ROUND((p.paymentamount - v.visitamount),2)) < 0.05) and DATE(p.paymentdate) != DATE(v.visitdate);

		-- before we go further, reload the data, so all made connections will not be available as todo data anymore.
	 CALL VisitPaymentCorrectSetUp(IN_MID, IN_FROMDATE, IN_TODATE);
	
	-- Now compare the payment with the total amount of visits. 
	INSERT INTO TMP_TMP_VisitTotal  select Customer_Id, Visit_ID, VisitDate, visitamount, mid from TMP_VisitTotal ;
	-- update the visit total 
	UPDATE  TMP_VisitTotal as vt
		SET VisitTotalAmount =
		 (select round(sum(tv.VisitAmount),2) from TMP_TMP_VisitTotal tv where tv.customer_id = vt.customer_id 
     group by tv.customer_id );
     
    INSERT INTO paymentvisit (version, mid, payment_id, visit_id, addedby, addedon, lasteditedby, lasteditedon)
	SELECT
  			0,
  			v.mid,
  		    p.payment_id,
            v.visit_id,
		   'Migration',
  			'2010-01-19 00:00',
	  	   'Migration',
  			'2010-01-19 00:00'
	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
	where(ABS(ROUND((p.paymentamount - v.VisitTotalAmount),2)) < 0.05) and DATE(p.paymentdate) != DATE(v.visitdate);

     -- setup the inforamtion	 
	 call VisitPaymentCorrectSetUp(IN_MID, IN_FROMDATE, IN_TODATE);		 

	-- look if there are multiple payments for one visit.
	-- mis use the table to load payment info.
	INSERT INTO TMP_TMP_VisitTotal  select Customer_Id, 0,paymentdate, paymentamount, mid from TMP_PaymentConnectVisit ;
	-- update the visit total 
	UPDATE  TMP_PaymentConnectVisit as pay
		SET PaymentTotalAmount =
		 (select round(sum(pay.VisitAmount),2) from TMP_TMP_VisitTotal tv where pay.customer_id = tv.customer_id 
     group by tv.customer_id );

	INSERT INTO paymentvisit (version, mid, payment_id, visit_id, addedby, addedon, lasteditedby, lasteditedon)
	SELECT
  			0,
  			v.mid,
  		    p.payment_id,
            v.visit_id,
		   'Migration',
  			'2010-01-19 00:00',
	  	   'Migration',
  			'2010-01-19 00:00'
	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
	where(ABS(ROUND((p.PaymentTotalAmount - v.VisitAmount),2)) < 0.05) ;

    call VisitPaymentCorrectSetUp(IN_MID, IN_FROMDATE, IN_TODATE);		
	-- anlyse other infromation
--	select p.payment_id, v.visit_id, p.customer_id c, patient_id, paymentdate, visitdate vd, p.paymentamount, v.visitamount, balance
--	FROM TMP_PaymentConnectVisit p join TMP_VisitTotal  v on p.customer_id = v.customer_id
--	ORDER BY c,vd		
-- where amount is the same ut different dates
--	FROM TMP_VisitTotal v JOIN TMP_PaymentConnectVisit p ON p.customer_id = v.customer_id
--	where p.paymentamount = v.visitamount order by visitdate ;		

	
	END $$

DELIMITER ;