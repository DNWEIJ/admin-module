-- add procedures if applciation handling becomes to slow
-- and stored procedure will be much more efficient


--
-- Definition of procedure `AppointmentOverview`
--

DELIMITER $$
  
DROP PROCEDURE IF EXISTS `AppointmentOverview` $$
CREATE  PROCEDURE `AppointmentOverview`(IN IN_MID bigint(20), IN IN_MLID bigint(20),  IN IN_FROMDATE varchar(50),IN IN_TODATE varchar(50), IN IN_ADDMONEY CHAR(1) )
BEGIN
	DROP TEMPORARY  table IF EXISTS TMP_APPOINTMENTVISIT; 
	DROP TEMPORARY  table IF EXISTS TMP_APPOINTMENTVISITAMOUNT;
	
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_APPOINTMENTVISIT (
		UNIQUE_TABLE_ID bigint(20) auto_increment,
		APPOINTMENT_ID bigint(20),
		MID bigint(20),
        MLID bigint (20),
        VISITDATE datetime,
        CANCELLED varchar(1) ,
        COMPLETED varchar(1) ,
        OTC varchar(1),
	    VISIT_ID bigint(20),
		ESTIMATEDTIME integer,
		VETERINARIAN varchar(70),
		PURPOSE varchar(100),
        ROOM varchar(15),
        `STATUS` varchar(1),
        PATIENT_ID bigint(20),
		 PETNAME varchar(40) ,
        SPECIES varchar(25) ,
        BREED varchar(50),
        CUSTOMER_ID bigint(20),
		LASTNAME varchar(20) ,
        FirstName varchar(20),
        SURNAME varchar(9),
        MIDDLEINITIAL varchar(1),
         primary key (UNIQUE_TABLE_ID)
	);
	
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_APPOINTMENTVISITAMOUNT (
		UNIQUE_TABLE_ID bigint(20),
        AMOUNT double(20,2),
        PAID double(20,2)
	);	

	 INSERT INTO TMP_APPOINTMENTVISIT
	 SELECT
	 	null,
        appointment.appointment_id,
        appointment.mid, 
        appointment.mlid           ,
        appointment.visitdate ,
        appointment.cancelled  ,
        appointment.completed ,
        appointment.OTC        ,        
        visit.visit_id              ,
        visit.estimatedtime         ,
		visit.veterinarian     ,
		visit.purpose      ,
		visit.room          ,
		visit.`status`       ,
		patient.patient_id,
		patient.petname     ,
		patient.species      ,
		patient.breed         ,
		customer.customer_id  ,
		customer.lastname   ,
	   customer.firstname , 
		customer.surname     ,
		customer.middleinitial
    from
        APPOINTMENT appointment
    inner join
        VISIT visit
            on appointment.APPOINTMENT_ID=visit.APPOINTMENT_ID
    inner join
        PATIENT patient
            on visit.PATIENT_ID=patient.PATIENT_ID
    inner join
        CUSTOMER
            on patient.CUSTOMER_ID=customer.CUSTOMER_ID
    WHERE  
        appointment.VISITDATE>=IN_FROMDATE 
        and appointment.VISITDATE<=IN_TODATE 
        and appointment.MID=IN_MID 
        and appointment.MLID=IN_MLID;

     IF (IN_ADDMONEY = 'Y') THEN 
	 	 INSERT INTO TMP_APPOINTMENTVISITAMOUNT
	 	  SELECT 
	 	 	UNIQUE_TABLE_ID,
	        ( select sum(total) from  lineitem 
	                where (lineitem.appointment_id = TMP_APPOINTMENTVISIT.appointment_id and lineitem.patient_id = TMP_APPOINTMENTVISIT.patient_id)) as total,
	        -- make a join between payment and paymentvisit and visit for the paid amount
			 ( select sum(amount) from paymentvisit pv 
			 join payment p on ( `pv`.`payment_id` =  `p`.`payment_id`)
			   join visit vst on (vst.visit_id = pv.visit_id) 
			   where vst.visit_id = TMP_APPOINTMENTVISIT.visit_id) as paid
			  from  TMP_APPOINTMENTVISIT;
	END IF;
	
	SELECT * FROM  TMP_APPOINTMENTVISIT ap LEFT JOIN TMP_APPOINTMENTVISITAMOUNT apva ON ap.UNIQUE_TABLE_ID = apva.UNIQUE_TABLE_ID;
	
END $$
DELIMITER ;



--
-- Definition of procedure `KasStelsel`
--

DELIMITER $$
  
DROP PROCEDURE IF EXISTS `KasStelsel` $$
CREATE  PROCEDURE `KasStelsel`(IN IN_MID bigint(20), IN IN_MLID bigint(20),  IN IN_FROMDATE varchar(50),IN IN_TODATE varchar(50))
BEGIN
	-- For test prupose:
	-- CALL KasStelsel(60, 61, '2010-01-01', '2010-04-01');
	-- replace the  direct compare with this one -> (a.MLID = COALESCE(IN_MLID, a.MLID))

	DECLARE V_TaxHigh 		double;
	DECLARE V_TaxLow 		double;
	DECLARE V_StartDate		datetime;
	
	DECLARE V_Applied 		double;
	DECLARE V_AppliedLowTax double;
	DECLARE V_AppliedHighTax double;
	DECLARE V_totalIncTax 	double;
	DECLARE V_AppointmentId bigint(20);
	DECLARE V_VisitId 		bigint(20);
	DECLARE V_PatientId		bigint(20);
	DECLARE V_PaymentId 	bigint(20);

	DECLARE V_IN_FROMDATE	varchar(50);
	DECLARE V_IN_TODATE		varchar(50);

	DECLARE V_PREVIOUS_PAYMENTID	bigint(20);
	
	/* declare the cursor var */
	DECLARE v_cc_notfound BOOL DEFAULT FALSE;	

	/* declare cursor  */
	DECLARE Visit_Cursor CURSOR FOR
			SELECT AppointmentId , VisitId , PatientId 
				FROM TMP_visits ORDER BY visitID ASC;

	DECLARE ResetPayment_Cursor CURSOR FOR
			SELECT PaymentId , VisitId , PatientId 
				FROM TMP_visits ORDER BY PaymentId;

	DECLARE continue handler for not found set v_cc_notfound := TRUE;

	SET V_IN_FROMDATE = concat(IN_FROMDATE , ' 00:00');
	SET V_IN_TODATE   = concat(IN_TODATE, ' 23:59');

	SELECT START       FROM THAU_MEMBER	     WHERE MEMBER_ID      = IN_MID INTO V_StartDate;
	
	/* Setup Tempory Tables */
	DROP TEMPORARY  table IF EXISTS TMP_visits;	/* calculate the TAX amount on it */
	DROP TEMPORARY  table IF EXISTS TMP_visitids; /* list of visits with an earlier payment */

	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_visits (
		OTC  char(1),      
		AppointmentId bigint(20),
	    VisitId bigint(20),
	    PatientId bigint(20),
	    PaymentId bigint(20),
	    CustomerId  bigint(20),
	    PaymentDate  datetime,
	    PaymentCreationDate  datetime,
	    VisitDate  datetime,
	    PaymentMethod int,
	    PaymentAmount double DEFAULT-1,
	    Applied double DEFAULT 0.0,
	    AppliedLowTax double DEFAULT 0.0,
	    AppliedHighTax double DEFAULT 0.0,
	    totalIncTax double DEFAULT 0.0
	);
	
	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_visitids (
	    VisitId bigint(20)
	);

	/* Load all data for the selected period from payment */
	/* do not use the payment date, but the payment creation date. We do not care when it has been paid, */
	/* we do want to know when it has been created in the system. That is the only stable date. It cannot be changed manually */
	INSERT INTO TMP_visits
		SELECT 
		    a.otc, 
			a.appointment_id, 
			v.visit_id, 
			v.patient_id,
			p.payment_id, 
			p.customer_id,
			p.paymentdate,
			p.addedon,
			a.visitdate,
			p.method, 
			p.amount, 
			0, 0, 0, 0
		FROM payment p  JOIN paymentvisit pv ON p.payment_id     = pv.payment_id
 						JOIN visit v 		 ON v.visit_id       = pv.visit_id
 						JOIN appointment a   ON a.appointment_id = v.appointment_id
		WHERE p.mid = IN_MID AND a.mid = IN_MID AND (a.mlid = COALESCE(IN_MLID, a.mlid)) -- a.mlid = IN_MLID  
		  AND p.addedon >= V_IN_FROMDATE  AND p.addedon <= V_IN_TODATE
		  AND p.amount > 0.00
		GROUP BY v.visit_id, v.patient_id;

	/* The payment can be connected to multiple visits. So we need to have only 1 paymentid, visit combination with the payment amount set */
	/* the others should be reset to zero */
	SET V_PREVIOUS_PAYMENTID = 0;
	set v_cc_notfound := false;
	OPEN Resetpayment_Cursor;
	CURSOR_LOOP: LOOP FETCH Resetpayment_Cursor into V_PaymentId, V_VisitId , V_PatientId ;
	    IF v_cc_notfound THEN
	      CLOSE Resetpayment_Cursor;
	      LEAVE cursor_loop;
	    END IF;
	    
	    IF V_PREVIOUS_PAYMENTID != V_PaymentId THEN
	    	SET V_PREVIOUS_PAYMENTID = V_PaymentId;
	    ELSE
		    UPDATE TMP_visits 
		    SET PaymentAmount  = 0.00
		    WHERE visitId      = V_visitId;
	    END IF;
	END LOOP;
	
	/* find out if there are visit already selected for the tax, this can be because there are multiple payments. */ 
	/* only have visits that have first payment within the payment period */ 
			
	/* retreive all visitid via the visits that are in the tmp_visit table and have an earlier payment date, then delete these visits */
	INSERT INTO TMP_visitids
		SELECT visitid
		FROM tmp_visits tv	JOIN paymentvisit pv ON  tv.visitid = pv.visit_id
		  					JOIN payment p 		 ON  tv.paymentid = p.payment_id
		  					WHERE p.addedon < V_IN_FROMDATE and p.mid = IN_MID
	GROUP BY p.paymentdate;
	
	DELETE from TMP_visits where visitid IN (SELECT visitid from TMP_visitids);

	/* Now ensure that visits are all AFTER the startdate of this member. */ 
	/* Empty the TMP_visitids, retreive all the visit ids that are before the startdate of the member, then delete them from the TMP_visitids */
	DELETE from TMP_visitids;
	INSERT INTO TMP_visitids
		SELECT visitid FROM tmp_visits tv	WHERE tv.VisitDate < V_StartDate;
	DELETE from TMP_visits where visitid IN (SELECT visitid from TMP_visitids);
	
	IF (IN_MID = 50 OR IN_MID = 60) THEN
		/* Empty the TMP_visitids, retreive all the visit that are within the first payment quartal of 2010; remove them from the visits, they are already paid  */
		DELETE from TMP_visitids;
		INSERT INTO TMP_visitids
			SELECT visitid FROM tmp_visits tv	WHERE tv.VisitDate < '2010-04-01 0:00';
		DELETE from TMP_visits where visitid IN (SELECT visitid from TMP_visitids);
	END IF;
	
	/* for every visit, create the  TAX information; it doesnot matter how many is paid, as long as there is a payment, then we
	 * sent the information to the TAX autorities */
	-- SELECT * FROM TMP_VISITS;
	set v_cc_notfound := false;
	OPEN Visit_Cursor;
	CURSOR_LOOP: LOOP FETCH Visit_Cursor into V_AppointmentId, V_VisitId , V_PatientId ;
	    IF v_cc_notfound THEN
	      CLOSE Visit_Cursor;
	      LEAVE cursor_loop;
	    END IF;
	    
		SELECT 
			COALESCE(sum(l.processingfee + (l.quantity * l.cost) ) , 0),
			COALESCE(sum( (IF(l.tax = 1,  l.costtaxportion , 0.0)) ) ,0) ,
			COALESCE(sum(l.processingfeeservicetaxportion + (IF(l.tax = 2, l.costtaxportion ,0.0)) ), 0) 
		 FROM lineitem l
		WHERE appointment_id = V_AppointmentId AND patient_id = V_PatientId and l.mid = IN_MID
	     INTO V_Applied, V_AppliedLowTax, V_AppliedHighTax;
	    
	    UPDATE TMP_visits 
	    SET Applied        = V_Applied, 
	    	AppliedLowTax  = V_AppliedLowTax, 
	    	AppliedHighTax = V_AppliedHighTax, 
	    	totalIncTax    = (V_Applied + V_AppliedLowTax + V_AppliedHighTax)
	    WHERE visitId      = V_visitId;
	END LOOP;

	--    PaymentMethod int,
	--    PaymentAmount double DEFAULT-1,
	--    Applied double DEFAULT 0.0,
	--    AppliedLowTax double DEFAULT 0.0,
	--    AppliedHighTax double DEFAULT 0.0,
	--    totalIncTax double DEFAULT 0.0
	
	-- Now add the miscellenious records, if any...
	INSERT INTO TMP_visits
	SELECT  
			'M',
			a.appointment_id, 
			-1, 
			-1,
			pm.paymentmisc_id, 
			-1,
			pm.addedon,
			pm.addedon,
			a.visitdate,
			 PM.Method, 			
			0, 0, 0, 0, 0
		FROM PaymentMisc PM
	    INNER JOIN Appointment A ON PM.Appointment_ID = A.Appointment_ID
	WHERE A.MID = IN_MID AND A.VisitDate >= V_IN_FROMDATE AND A.VisitDate <= V_IN_TODATE;
		
	UPDATE TMP_visits set PaymentAmount = 0.0  where PaymentAmount is null;

	 /* create for the miscellenous payment also the tax parts */
	UPDATE TMP_visits PCB
		SET paymentamount  = COALESCE((SELECT SUM(Total) as total FROM LineItemsMisc l where PCB.AppointmentId = l.Appointment_ID), 0.00),
		    totalIncTax    = COALESCE((SELECT SUM(Total) as total FROM LineItemsMisc l where PCB.AppointmentId = l.Appointment_ID), 0.00),
		    Applied 	   = COALESCE((SELECT SUM(IF(`quantity` > 0.00, (l.processingfee + (l.quantity * l.cost)), 0.00 )) 										 as appl  FROM LineItemsMisc l WHERE PCB.AppointmentId = l.Appointment_ID),0.00),
			AppliedLowTax  = COALESCE((SELECT SUM(IF(`quantity` > 0.00,  l.costtaxportion, 0.00))         as applg FROM LineItemsMisc l WHERE PCB.AppointmentId = l.Appointment_ID AND l.Tax = 1), 0.00),
	        AppliedHighTax = COALESCE((SELECT SUM(IF(`quantity` > 0.00, (l.processingfeeservicetaxportion + l.costtaxportion), 0.00)) as appst FROM LineItemsMisc l WHERE PCB.AppointmentId = l.Appointment_ID AND l.Tax = 2), 0.00)
		WHERE CustomerId = -1;


	  /* return the information */
  SELECT
  		OTC, 
	    PaymentMethod ,
	    PaymentAmount ,
	    Applied ,
	    AppliedLowTax ,
	    AppliedHighTax ,
	    totalIncTax 
  FROM  TMP_visits;
  DROP TEMPORARY  table IF EXISTS TMP_visits;	/* calculate the TAX amount on it */
  DROP TEMPORARY  table IF EXISTS TMP_visitids; /* list of visits with an earlier payment */
	
	
	-- SELECT * FROM TMP_VISITS;
END $$
DELIMITER ;
	
	
-- End KasStelsel
	
	
	
--
-- Definition of procedure `CashBasisOverview`
--


	DELIMITER $$

	DROP PROCEDURE IF EXISTS `CashBasisOverview` $$
	CREATE  PROCEDURE `CashBasisOverview`(IN IN_MID bigint(20), IN IN_MLID bigint(20), IN IN_FROMDATE datetime, IN IN_TODATE datetime)
	BEGIN

	-- For test prupose:
	-- CALL CashBasisOverview(209, 238, '2009-01-26 00:00', '2009-01-27 23:59');
	--

	/* declare variables to retreive record from cursor Customer_Cursor*/
	DECLARE V_CC_Payment_ID    bigint(20);
	DECLARE V_CC_Customer_ID   bigint(20);
	DECLARE V_CC_PaymentDate   datetime;
	DECLARE V_CC_PaymentAmount double;

	/* declare variables to retreive record from cursor Charges_FullPayoff_Cursor and Charges_Cursor*/
	DECLARE V_CFC_ID bigint(20);
	DECLARE V_CFC_ChargeDate datetime;
	DECLARE V_CFC_LineItem_ID bigint(20);
	DECLARE V_CFC_Tax  double;
	DECLARE V_CFC_Quantity double;
	DECLARE V_CFC_Cost double;
	DECLARE V_CFC_ProcessingFee double;
	DECLARE V_CFC_ProcessingFeeServiceTaxPortion double;
	DECLARE V_CFC_Total double;

	/* declare the cursor var */
	DECLARE v_cc_notfound BOOL DEFAULT FALSE;
	DECLARE V_TEMPVALUE  double;
	DECLARE SELECT_PaymentDate datetime;

	 /* define variables needed for calculations */
	DECLARE SumCharges double;
	DECLARE SumPayments double;
	DECLARE WorkSumPayments double;
	DECLARE V_Percentage  double;


	DECLARE Customer_Cursor CURSOR FOR
	    SELECT PaymentId, CustomerId, PaymentDate, PaymentAmount
	      FROM TMP_PaymentsWithCashBasis ORDER BY CustomerId, PaymentId;

	/* declare cursor  Charges_Cursor*/
	DECLARE Charges_FullPayoff_Cursor CURSOR FOR
			SELECT ID, ChargeDate, LineItem_ID, Tax, Quantity, Cost, ProcessingFee, ProcessingFeeServiceTaxPortion, Total
				FROM TMP_Charge
				ORDER BY ChargeDate, LineItem_ID;

	/* declare cursor Charges_Cursor */
	DECLARE Charges_Cursor CURSOR FOR
			SELECT ID, ChargeDate, LineItem_ID, Tax, Quantity, Cost, ProcessingFee, ProcessingFeeServiceTaxPortion, Total
				FROM TMP_Charge ORDER BY ID ASC;

	DECLARE continue handler for not found set v_cc_notfound := TRUE;

	/* Setup Tempory Teables */
	DROP TEMPORARY  table IF EXISTS TMP_PaymentsWithCashBasis;

	CREATE TEMPORARY  TABLE IF NOT EXISTS TMP_PaymentsWithCashBasis (
	    PaymentId bigint(20),
	    CustomerId  bigint(20),
	    PaymentDate  datetime,
	    OwnerName varchar (55) DEFAULT'-- Misc Transaction --',

	    PaymentMethod int,
	    PaymentAmount double DEFAULT-1,

	    Applied double DEFAULT 0.0,
	    AppliedGoodTax double DEFAULT 0.0,
	    AppliedServiceTax double DEFAULT 0.0,
	    BeforeCreditsDebits double     DEFAULT 0.0,
	    BeforeCreditsDebitsForMLID double DEFAULT 0.0,
	    AfterCreditsForMLID double DEFAULT 0.0,

	    Balance double DEFAULT -1,
	    BalanceAtMLID double DEFAULT -1,
	    BalanceAsOf double DEFAULT -1,
	    BalanceAsOfAtMLID double  DEFAULT -1
	);

	DROP  TEMPORARY table IF EXISTS TMP_Charge;

	CREATE TEMPORARY TABLE IF NOT EXISTS TMP_Charge (
			Chargedate datetime,
	    ID bigint(20) AUTO_INCREMENT,
			LineItem_ID bigint(20),
			Tax int,
			Quantity double default 0.0,
			Cost double default 0.0,
			ProcessingFee double default 0.0,
			ProcessingFeeServiceTaxPortion double default 0.0,
			Total double default 0.0,
	     PRIMARY KEY (`ID`)
	 );

	DROP   TEMPORARY table IF EXISTS TMP_Payment;

	CREATE TEMPORARY TABLE IF NOT EXISTS TMP_Payment (
		PaymentDate datetime,
		PaymentID bigint(20),
		PaymentAmount double default 0.0
	);

	-- print out values for debug purpose
	-- select IN_MID, IN_MLID, IN_FROMDATE, IN_TODATE from dual
	  /* cleanup old data */
	-- TRUNCATE TABLE TMP_PaymentsWithCashBasis;

	/* Load all data for the selected period from payment; directly set the balance values correclty */
		INSERT INTO TMP_PaymentsWithCashBasis
			SELECT  pay.Payment_ID,
		    pay.Customer_id,
		    pay.PaymentDate,
		    FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial) AS OwnerName,
		    pay.Method,
		    pay.Amount,
		     0,
		     0,
			  0,
		     0,
		     0,
		     0,
			  0,
		     0,
		     0,
		     0
				FROM Payment pay
					inner JOIN customer c ON c.customer_id = pay.customer_Id
				 WHERE pay.MLID = IN_MLID AND c.MID = IN_MID AND pay.PaymentDate >= IN_FROMDATE AND Pay.PaymentDate <= IN_TODATE;



	  /* update data with the balance information */
	UPDATE  TMP_PaymentsWithCashBasis as PCB
			SET Balance =
			  round( ( COALESCE((select Sum(Amount) FROM payment p WHERE p.customer_id = PCB.customerId),0) -
			     COALESCE((select Sum(Total) FROM LineItem LI INNER JOIN Patient pat ON LI.Patient_ID = pat.Patient_ID WHERE pat.customer_id = PCB.customerId),0)), 2),
		BalanceAtMLID =FuncOwnerBalanceAtMLID(PCB.customerId,null,IN_MLID),
		BalanceAsOf =FuncOwnerBalanceAtMLID(PCB.customerId,PCB.PaymentDate,null),
		BalanceAsOfAtMLID =FuncOwnerBalanceAtMLID(PCB.customerId,PCB.PaymentDate,IN_MLID);

	/* all data is retreived :: Set for EVERY payment the credits/debits by calculating the balance according to the  payment date */
		UPDATE TMP_PaymentsWithCashBasis PCB
			SET BeforeCreditsDebits =FuncOwnerBalanceAtMLID(PCB.customerId,PCB.PaymentDate,null),
				 BeforeCreditsDebitsForMLID =FuncOwnerBalanceAtMLID(PCB.customerId,PCB.PaymentDate,IN_MLID);

	/* Start with getting payment and lineitems to match */
	 open Customer_Cursor;
	 cursor_loop: LOOP FETCH Customer_Cursor into V_CC_Payment_ID, V_CC_Customer_ID, V_CC_PaymentDate, V_CC_PaymentAmount;
	    if v_cc_notfound then
	      CLOSE Customer_Cursor;
	      LEAVE cursor_loop;
	    end if;

	    /* cleanup old data */
	    TRUNCATE TABLE TMP_Charge;
	    TRUNCATE TABLE TMP_Payment;

	    /* set date correct to maximum time */
	    IF (V_CC_PaymentDate is not null) THEN
	      SET SELECT_PaymentDate =DATE(V_CC_PaymentDate);
	      SET SELECT_PaymentDate = ADDTIME(SELECT_PaymentDate, '23:59');
	    END IF;

	     /* load in veterinary line item charges for the specid customer from customer_cursor */
			INSERT INTO TMP_Charge (Chargedate,id,LineItem_ID,Tax,Quantity,Cost,ProcessingFee,ProcessingFeeServiceTaxPortion,Total)
	     -- INSERT INTO TMP_Charge
				SELECT DISTINCT A.VisitDate,0, LI.LineItem_ID,  LI.Tax, LI.Quantity,LI.Cost, LI.ProcessingFee, LI.ProcessingFeeServiceTaxPortion, LI.Total
					FROM LineItem LI
						INNER JOIN Visit V       ON LI.Patient_ID = V.Patient_ID AND LI.Appointment_ID = V.Appointment_ID
						INNER JOIN Patient P     ON V.Patient_ID = P.Patient_ID
						INNER JOIN Appointment A ON LI.Appointment_ID = A.Appointment_ID
					WHERE P.Customer_ID = V_CC_Customer_ID AND A.MLID = IN_MLID AND A.VisitDate <= SELECT_PaymentDate;

			/* load all payments for owner at mlid at or before payment date */
			INSERT INTO TMP_Payment
				SELECT p.PaymentDate, p.Payment_ID, p.Amount	FROM Payment p
					WHERE p.Customer_id =V_CC_Customer_ID AND p.MLID = IN_MLID AND PaymentDate <= SELECT_PaymentDate;

			/* remove the current payment and all payment on the same day that are made after this payment */
			DELETE FROM TMP_Payment WHERE PaymentID  >= V_CC_Payment_ID;

	    SET SumCharges = 0.00;
	    SET SumPayments = 0.00;
	    SET WorkSumPayments = 0.00;
		SET V_Percentage = 0.00;

	    /* retreive the summation of payment and outstandings  */
			SELECT SUM(Total) FROM TMP_Charge INTO SumCharges;
			SELECT SUM(PaymentAmount) FROM TMP_Payment INTO SumPayments;

			SET WorkSumPayments = SumPayments;

	    /* start the compare of payment and line items, find out which line items are covered by a payment and which not */
			OPEN Charges_FullPayoff_Cursor;
			cfc_cursor_loop: LOOP FETCH Charges_FullPayoff_Cursor INTO V_CFC_ID, V_CFC_ChargeDate, V_CFC_LineItem_ID, V_CFC_Tax,V_CFC_Quantity, V_CFC_Cost, V_CFC_ProcessingFee, V_CFC_ProcessingFeeServiceTaxPortion, V_CFC_Total;
	      IF v_cc_notfound THEN
		CLOSE Charges_FullPayoff_Cursor;
		set v_cc_notfound := false;
		LEAVE cfc_cursor_loop;
	      END IF;


			IF (WorkSumPayments >= V_CFC_Total) THEN
			BEGIN
		 /* the payment covers this line item amount, so line item can be deleted from overview it is accounted for */
		  SET WorkSumPayments = WorkSumPayments - V_CFC_Total;
			  UPDATE   TMP_Charge set total = 0.00 WHERE ID =V_CFC_ID;
			END;
			  ELSEIF (WorkSumPayments > 0.00) THEN
		 /* prior payment covers part of line item total; so reduce with the amount that is paid; only keep the part that is not paid */
				SET v_percentage = 1-(WorkSumPayments / V_CFC_Total);
				UPDATE TMP_Charge SET Cost = Cost * V_Percentage,
				                     ProcessingFee = ProcessingFee * V_Percentage,
						             ProcessingFeeServiceTaxPortion = ProcessingFeeServiceTaxPortion * V_Percentage,
								Total = Total * V_Percentage
							WHERE ID = V_CFC_ID;
				SET WorkSumPayments = 0.00;
	      END IF;
	    END LOOP;


	    /* remove paid charges; do this outside cursor, else cursor is going nuts */
			DELETE FROM TMP_Charge WHERE Total = 0.00;


			/* if credits exists apply them now */
			SET WorkSumPayments = 0.00;
			IF ((SumCharges - SumPayments) < 0) THEN
	      SET WorkSumPayments = ( SumPayments - SumCharges);
	    END IF;

			/* set credits plus current payment to apply now */
	    /* We have found what is paid wihth the previous payments. Know start to pay all line items with the current payment from the customer_cursor */
			SET WorkSumPayments = WorkSumPayments + V_CC_PaymentAmount;


	    /* start the processing of all the line items that can be paid with the current payment from the customer_cursopr */
			OPEN Charges_Cursor;
			Charges_Cursor_loop: LOOP FETCH Charges_Cursor INTO V_CFC_ID, V_CFC_ChargeDate, V_CFC_LineItem_ID, V_CFC_Tax,V_CFC_Quantity, V_CFC_Cost, V_CFC_ProcessingFee, V_CFC_ProcessingFeeServiceTaxPortion, V_CFC_Total;
	      IF v_cc_notfound THEN
		CLOSE Charges_Cursor;
		/* inner loop so reset the flag for outer loop */
		set v_cc_notfound := false;
		LEAVE Charges_Cursor_loop;
	      END IF;
			IF (WorkSumPayments > 0) THEN
				IF (WorkSumPayments >= V_CFC_Total) THEN
					SET V_Percentage = 1.00;
				SET WorkSumPayments = WorkSumPayments - V_CFC_Total;
			    ELSE
				  SET V_Percentage = (WorkSumPayments / V_CFC_Total);
					SET WorkSumPayments = 0;
			END IF;

		IF V_CFC_Quantity > 0 THEN
		   SET V_TEMPVALUE = V_CFC_Total - ((V_CFC_Cost * V_CFC_Quantity) + V_CFC_ProcessingFee);
		ELSE
			SET V_TEMPVALUE = V_CFC_Total;
		      END IF;

		IF (V_CFC_Tax = 0) THEN
				/* no tax */
				UPDATE TMP_PaymentsWithCashBasis
			SET Applied = Applied + (V_CFC_Total * V_Percentage)
					WHERE PaymentID = V_CC_Payment_ID AND customerID = V_CC_Customer_ID AND PaymentDate = V_CC_PaymentDate;
				ELSE
		  IF (V_CFC_Tax = 1) THEN
				  /* good tax */
					  UPDATE TMP_PaymentsWithCashBasis
					  SET Applied = Applied + (V_CFC_Total * V_Percentage),
			  AppliedGoodTax = AppliedGoodTax + ((V_TEMPVALUE - V_CFC_ProcessingFeeServiceTaxPortion) * V_Percentage),
			 AppliedServiceTax = AppliedServiceTax + (V_CFC_ProcessingFeeServiceTaxPortion * V_Percentage)
					  WHERE PaymentID = V_CC_Payment_ID AND Customerid = V_CC_Customer_ID AND PaymentDate = V_CC_PaymentDate;
				ELSE
				/* service tax */
				UPDATE TMP_PaymentsWithCashBasis
						SET Applied = Applied + (V_CFC_Total * V_Percentage),
			  AppliedServiceTax = AppliedServiceTax + (V_TEMPVALUE * V_Percentage)
						  WHERE PaymentID = V_CC_Payment_ID AND customerID = V_CC_Customer_ID AND PaymentDate = V_CC_PaymentDate;
		  END IF;

		END IF;

				DELETE FROM TMP_Charge WHERE ID = V_CFC_ID;
	      ELSE
		DELETE FROM TMP_Charge;
	      END IF; /* close the WorkSumPayments */
	    END LOOP; /* charges cursor */
	 END LOOP; /* customer loop */


	/* know we only need to add the miscellenious transaction to the table */
		INSERT INTO  TMP_PaymentsWithCashBasis
			SELECT   a.appointment_id,
		     -1,
		     A.VisitDate,
		     '-- Misc Transaction --',
		     PM.Method,
		     (select Sum(Total) as total FROM LineItemsMisc where Appointment_ID = A.Appointment_ID),
		     0,
		     0,
		     0,
		     0,
		     0,
		     0,
		     0,
		     0,
		     0,
		     0
				FROM PaymentMisc PM
			    INNER JOIN Appointment A ON PM.Appointment_ID = A.Appointment_ID
					INNER JOIN thau_memberlocal ML ON A.MLID = ML.MEMBERlocal_ID
			WHERE ML.MEMBER_ID = IN_MID AND ML.MEMBERLOCAL_ID = IN_MLID AND A.VisitDate >= IN_FROMDATE AND A.VisitDate <= IN_TODATE;

	    UPDATE TMP_PaymentsWithCashBasis set PaymentAmount = 0.0  where PaymentAmount is null;

	 /* create for the miscellenous payment also the tax parts */
		UPDATE TMP_PaymentsWithCashBasis PCB
			SET Applied = COALESCE((SELECT SUM(LI.Total) as appl FROM LineItemsMisc LI WHERE PCB.PaymentId = LI.Appointment_ID),0),
				AppliedGoodTax = COALESCE((SELECT SUM(if (`quantity` > 0.00,  (Total - ((Cost * Quantity) + ProcessingFee)), Total)) as applg FROM LineItemsMisc LI WHERE PCB.PaymentId = LI.Appointment_ID AND LI.Tax = 1),0),
		/*                 select sum(if (`quantity` > 0.00,  (Total - ((Cost * Quantity) + ProcessingFee)), Total)) FROM LineItemsMisc */
	      AppliedServiceTax = COALESCE((SELECT SUM(if (`quantity` > 0.00,  (Total - ((Cost * Quantity) + ProcessingFee)), Total)) as appst FROM LineItemsMisc LI WHERE PCB.PaymentId = LI.Appointment_ID AND LI.Tax = 2),0)
			WHERE CustomerId = -1;


	  /* return the information */
	  select * from TMP_PaymentsWithCashBasis ORDER BY PaymentDate, OwnerName, PaymentID;
	   drop TEMPORARY TABLE  TMP_PaymentsWithCashBasis;


	END $$

	DELIMITER ;
--
-- Definition of procedure `PaymentAccountsReceivableOwnerLoop`
--


	DELIMITER $$

	DROP PROCEDURE IF EXISTS `PaymentAccountsReceivableOwnerLoop` $$
	CREATE  PROCEDURE `PaymentAccountsReceivableOwnerLoop`(IN P_Mid BigInt(20))
	BEGIN

	DECLARE owner_id BigInt(20);
	DECLARE temp_owner_id BigInt(20);
	DECLARE ownerLastName varchar(55);
	DECLARE ownerFirstName varchar(55);
	DECLARE ownerSurName varchar(9);
	DECLARE ownerMiddleInitial varchar(1);
	DECLARE ownerName varchar(100);
	DECLARE tempWorkColumn Double;
	DECLARE no_more_customers int default 0;
	DECLARE Customer_Cursor CURSOR FOR 
		SELECT distinct Customer_Id FROM Customer 
		WHERE MID = P_Mid AND FuncOwnerBalanceAtMLID(Customer_Id,CONCAT(DATE(now()),' 23:59:59'),null)<0;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_customers=1;

		CREATE TEMPORARY TABLE IF NOT EXISTS 
		   TEM_Balance(
		      OwnerID BigInt(20),
		      Owner varchar(55),
		      LastName varchar(20),
		      FirstName varchar(20),
		      TotalAR Double,
		      Last30 Double,
		      From31To60 Double,
		      From61To90 DOUBLE,
		      Over90 DOUBLE,
		      WorkColumn DOUBLE);

		OPEN Customer_Cursor;
		FETCH Customer_Cursor INTO temp_owner_id;
		WHILE no_more_customers <> 1 do

		SELECT Customer_Id,LastName,FirstName,Surname,MiddleInitial 
		FROM Customer 
		WHERE Customer_Id = temp_owner_id INTO owner_id,ownerLastName,ownerFirstName,ownerSurName,ownerMiddleInitial;
		
		SET ownerName = FuncFormatOwnerName(ownerLastName,ownerFirstName,ownerSurName,ownerMiddleInitial);
		
		INSERT INTO TEM_Balance(OwnerID,Owner,LastName,FirstName)VALUES(owner_id,ownerName,ownerLastName,ownerFirstName);
		
		UPDATE TEM_Balance
		   SET TotalAR = ABS(FuncOwnerBalanceAtMLID(temp_owner_id,CONCAT(DATE(now()),' 23:59:59'),null)),
		   Last30 = FuncOwnerChargesDuring(owner_id,DATE(DATE_ADD(now(),INTERVAL -30 DAY)),DATE(now())),
		   From31To60 = FuncOwnerChargesDuring(owner_id,DATE(DATE_ADD(now(),INTERVAL -60 DAY)),DATE(DATE_ADD(now(),INTERVAL -31 DAY))),
		   From61To90 = FuncOwnerChargesDuring(owner_id,DATE(DATE_ADD(now(),INTERVAL -90 DAY)),DATE(DATE_ADD(now(),INTERVAL -61 DAY))),
		   Over90 =  FuncOwnerChargesDuring(owner_id,DATE(DATE_ADD(now(),INTERVAL -100 YEAR)),DATE(DATE_ADD(now(),INTERVAL -91 DAY)))
		   WHERE OwnerID = owner_id;

		SELECT TotalAR FROM TEM_Balance where OwnerID = owner_id INTO tempWorkColumn;
		UPDATE TEM_Balance SET WorkColumn = tempWorkColumn WHERE OwnerID = owner_id;

		-- update over last 30 days charges accordingly
		UPDATE TEM_Balance
			SET WorkColumn = (WorkColumn - Last30),Last30 = TotalAR
			WHERE (TotalAR <= Last30) AND (WorkColumn > 0) AND (Last30 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = WorkColumn - Last30,Last30 = TotalAR - Last30
			WHERE (TotalAR > Last30) AND (WorkColumn > 0) AND (Last30 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = 0,Last30 = TotalAR
			WHERE (WorkColumn <= 0) AND (Last30 > 0) AND OwnerID = owner_id;

		-- zero out remainder of work column <= 0
		UPDATE TEM_Balance
			SET From31To60 = 0,From61To90 = 0,Over90 = 0
			WHERE (WorkColumn <= 0) AND OwnerID = owner_id;

		-- update over 31-60 days charges accordingly
		UPDATE TEM_Balance
			SET WorkColumn = (WorkColumn - From31To60),From31To60 = TotalAR
			WHERE (TotalAR <= From31To60 ) AND (WorkColumn > 0) AND (From31To60 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = WorkColumn - From31To60,From31To60 = TotalAR-From31To60
			WHERE (TotalAR > From31To60) AND (WorkColumn > 0) AND (From31To60 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = 0,From31To60 = TotalAR - Last30
			WHERE (WorkColumn <= 0) AND (From31To60 > 0) AND OwnerID = owner_id;

		-- zero out remainder of work column <= 0
		UPDATE TEM_Balance SET From61To90 = 0,Over90 = 0 WHERE (WorkColumn <= 0) AND OwnerID = owner_id;

		-- update over 61-90 days charges accordingly
		UPDATE TEM_Balance
			SET WorkColumn = (WorkColumn-From61To90),From61To90 = TotalAR
			WHERE (TotalAR <= From61To90) AND (WorkColumn > 0) AND (From61To90 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = WorkColumn-From61To90,From61To90 = TotalAR - From61To90
			WHERE (TotalAR > From61To90) AND (WorkColumn > 0) AND (From61To90 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = 0,From61To90 = TotalAR-Last30-From31To60
			WHERE (WorkColumn <= 0) AND (From61To90 > 0) AND OwnerID =  owner_id;

		-- zero out remainder of work column <= 0
		UPDATE TEM_Balance SET Over90 = 0 WHERE (WorkColumn <= 0) AND OwnerID =  owner_id;

		-- update over 61-90 days charges accordingly
		UPDATE TEM_Balance
			SET WorkColumn = (WorkColumn-Over90),Over90 = TotalAR
			WHERE (TotalAR <= Over90) AND (WorkColumn > 0) AND (Over90 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = WorkColumn - Over90,Over90 = TotalAR-Over90
			WHERE (TotalAR > Over90) AND (WorkColumn > 0) AND (Over90 > 0) AND OwnerID = owner_id;
		UPDATE TEM_Balance
			SET WorkColumn = 0,Over90 = TotalAR-Last30 - From31To60 - From61To90
			WHERE (WorkColumn <= 0) AND (Over90 > 0) AND OwnerID = owner_id;

	      FETCH Customer_Cursor INTO temp_owner_id;
	      END while;
	      CLOSE Customer_Cursor;

	      SELECT * FROM TEM_Balance ORDER BY LastName,FirstName;
	      DROP TEMPORARY table IF EXISTS TEM_Balance;

	END $$

	DELIMITER ;
--
-- Definition of procedure `paymentCalculate`
--
DELIMITER $$

DROP PROCEDURE IF EXISTS `paymentCalculate` $$
CREATE PROCEDURE `paymentCalculate`(IN p_mid BIGINT(20),IN p_mlid BIGINT(20),
            IN p_tempDate varchar(50),IN p_fromDate varchar(50),IN p_toDate varchar(50))
BEGIN

	DECLARE owner_id bigint(20);
	DECLARE TotalCredits double default 0;
	DECLARE TotalDerbits double default 0;
	DECLARE Credits_Temp double default 0;
	DECLARE PaymentsPeriodPayments double default 0;
	DECLARE TotalPeriodPayment double default 0;
	DECLARE LineMiscTotal double default 0;
	DECLARE no_more_customers int default 0;
	DECLARE Customer_Cursor CURSOR FOR SELECT DISTINCT Customer_ID FROM Customer WHERE MID = p_mid;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_customers=1;

     SET no_more_customers = 0;
     SET TotalCredits =  0;
     SET TotalDerbits =  0;
     CREATE TEMPORARY TABLE IF NOT EXISTS TMP_Payment_Calculate(Credits double,Derbits double,PeriodPayments double);

     OPEN Customer_Cursor;
     FETCH Customer_Cursor INTO owner_id;
     WHILE no_more_customers <> 1 do

     	IF(P_tempDate is not null) THEN
	    	 SET Credits_Temp =  FuncOwnerBalanceAtMLID(owner_id,CONCAT(p_tempDate,' 23:59:59'),p_mlid);

	     	IF(Credits_Temp>0) THEN
    	    	   SET TotalCredits = TotalCredits + Credits_Temp;
     		END IF;

	     	IF(Credits_Temp<0) THEN
    	    	   SET TotalDerbits = TotalDerbits + ABS(Credits_Temp);
     		END IF;
		END IF;

     	IF(p_fromDate is not null && p_toDate is not null) THEN
        	SELECT Sum(P.Amount)
		  	 FROM payment P
		  	WHERE (P.customer_id = owner_id)
              AND (P.MLID = COALESCE(P_mlid, P.MLID))
              AND (P.PaymentDate >= CONCAT(p_fromDate,' 00:00:00'))
              AND (P.PaymentDate <= CONCAT(p_toDate,' 23:59:59')) into PaymentsPeriodPayments;
              
			IF(PaymentsPeriodPayments IS NULL) THEN
		    	SET PaymentsPeriodPayments = 0;
        	END IF;

	        SET TotalPeriodPayment = TotalPeriodPayment + PaymentsPeriodPayments;
    	END IF;

     	FETCH Customer_Cursor INTO owner_id;
     END while;

     CLOSE Customer_Cursor;

     IF(p_fromDate is not null && p_toDate is not null) THEN
     	SELECT Sum(LIM.Total)
		  FROM LineItemsMisc LIM
		  INNER JOIN Appointment A ON (LIM.Appointment_ID = A.Appointment_ID)
		  INNER JOIN thau_MemberLocal ML ON (A.MLID = ML.memberlocal_id)
		 WHERE (ML.member_id =p_mid)
           AND (ML.memberlocal_id = COALESCE(p_mlid, A.MLID))
           AND A.VisitDate >= CONCAT(p_fromDate,' 00:00:00')
           AND A.VisitDate <= CONCAT(p_toDate,' 23:59:59') Into LineMiscTotal;

        IF(LineMiscTotal IS NULL) then
		    SET LineMiscTotal = 0;
         END IF;
         SET  TotalPeriodPayment = TotalPeriodPayment + LineMiscTotal;
     END IF;

     INSERT 
       INTO TMP_Payment_Calculate(Credits,Derbits,PeriodPayments)
     VALUES(Round(TotalCredits,2), Round(TotalDerbits,2), Round(TotalPeriodPayment,2));
    
     SELECT * FROM TMP_Payment_Calculate;

	 DROP TEMPORARY TABLE TMP_Payment_Calculate;
END $$

DELIMITER ;
--
-- Definition of procedure `PatientSOAP`
--
	DELIMITER $$

	DROP PROCEDURE IF EXISTS `PatientSOAP` $$
	CREATE  PROCEDURE `PatientSOAP`(p_patientId BIGINT(20), p_mid BIGINT(20))
	BEGIN

	CREATE TEMPORARY TABLE IF NOT EXISTS TMP (
	    petName varchar (55),
	    visitId bigint(20),
	    purpose varchar (500),
	    comments varchar (1024),
	    veterinarian varchar (55),
	    appointmentId  bigint(20),
	    visitDate  datetime,
	    clinic varchar (200),
	    purposeType varchar (1)
	);

	INSERT INTO TMP
			select  b.petName,c.visit_id ,c.purpose ,c.comments,c.veterinarian,d.appointment_id ,d.visitDate,f.clinic,'0' AS PurposeType
	from customer a inner join patient b on a.customer_id=b.customer_id
	inner join visit c on c.patient_id=b.patient_id
	inner join appointment d on  d.appointment_id=c.appointment_id
	inner join thau_memberlocal f on d.mlid=f.MEMBERLOCAL_ID
	where b.patient_id=p_patientId
	and b.mid=p_mid;
	INSERT INTO TMP
	SELECT b.petName,0, a.purpose ,a.notes as comments,a.STAFFMEMBER as veterinarian,a.notepad_id as appointment_id,a.notedate as visitDate,'' as clinic,'1' AS PurposeType FROM notepad a
	inner join patient b on a.patient_id=b.patient_id
	where b.patient_id=p_patientId
	and a.mid=p_mid;
	select * from TMP order by visitDate desc;

	 DROP  TEMPORARY table IF EXISTS TMP;

	END $$

	DELIMITER ;
	
	
	
--
-- Definition of procedure `CalculateDiagnoseCount`
--
DELIMITER $$

DROP PROCEDURE IF EXISTS `CalculateDiagnoseCount` $$
CREATE  PROCEDURE `CalculateDiagnoseCount`(IN Input_Mid BigInt(20),IN Input_Mlid BigInt(20),IN FromDate varchar(20),IN ToDate varchar(20))
BEGIN

CREATE TEMPORARY TABLE IF NOT EXISTS TEMP_DiagnoseCount(Diagnose_Name varchar(200),Diagnose_Count Bigint(20));

Insert INTO TEMP_DiagnoseCount
        select ld.nomenclature as diagnosis,count(ld.nomenclature) as countDiagnosis
          from  lookupdiagnose ld
          inner join diagnose d on ld.lookupdiagnosis_id = d.lookupdiagnosis_id
          INNER JOIN Appointment A ON (d.Appointment_ID = A.Appointment_ID)
          INNER JOIN thau_MemberLocal ML ON (A.MLID = ML.memberlocal_id)
          WHERE (ML.memberlocal_id = coalesce(Input_Mlid,ml.memberlocal_id)) AND (ML.member_id = Input_Mid)
          AND (A.VisitDate >= CONCAT(FromDate,' 00:00:00'))
          AND (A.VisitDate <= CONCAT(ToDate,' 23:59:59'))
          group by ld.nomenclature
          order by count(ld.nomenclature) desc;

     select * from TEMP_DiagnoseCount;

     DROP TEMPORARY table IF EXISTS TEMP_DiagnoseCount;

END $$

DELIMITER ;	

--
-- Definition of procedure `CalculateProductServiceCount`
--
DELIMITER $$

DROP PROCEDURE IF EXISTS `CalculateProductServiceCount` $$
CREATE  PROCEDURE `CalculateProductServiceCount`(IN i_mid Bigint(20),
                                 IN i_mlid Bigint(20),IN fromDate varchar(20),IN toDate varchar(20))
BEGIN

   DECLARE temp_CategoryID BigInt(20);
   DECLARE categoryName varchar(50);
   DECLARE no_more_category int default 0;
   DECLARE Category_Cursor CURSOR FOR 
      SELECT distinct lookupcostingcategory_id,category FROM lookupcostingcategory
       WHERE mid in (-1,i_mid) ORDER BY lookupcostingcategory_id;
       
   DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_category=1;

   CREATE TEMPORARY TABLE IF NOT EXISTS TMP (service varchar (50),servicecount DECIMAL(10,1), c_id bigint(20),c_name varchar(50));
   OPEN Category_Cursor;
   FETCH Category_Cursor INTO temp_CategoryID,categoryName;
   WHILE no_more_category <> 1 do

      INSERT INTO TMP
                select li.nomenclature as service, SUM(li.QUANTITY) AS servicecount, temp_CategoryID as id,categoryName as name
                from lookupcostingcategory lcc
                inner join lineitem li on lcc.lookupcostingcategory_id = li.category_id
                inner join appointment a on li.appointment_id = a.appointment_id
                inner join thau_memberlocal ml on (a.mlid = ml.memberlocal_id)
                where (ml.member_id = i_mid) and (li.category_id = temp_CategoryID)
		        and (ml.memberlocal_id = coalesce(i_mlid,ml.memberlocal_id))
                and a.visitdate >= concat(fromDate,' 00:00:00')
                and a.visitdate <= concat(toDate,' 23:59:59')
		        group by li.nomenclature order by SUM(li.QUANTITY) desc  limit 0,50;

		FETCH Category_Cursor INTO temp_CategoryID,categoryName;
    END WHILE;

    Select * from TMP;
    DROP  TEMPORARY table IF EXISTS TMP;
END $$

DELIMITER ;



--
-- Definition of procedure `PaymentListing`
--
DELIMITER $$

DROP PROCEDURE IF EXISTS `PaymentListing` $$
CREATE PROCEDURE `PaymentListing`(in i_mid bigint(20),in i_mlid bigint(20),in fromDate varchar(20),in toDate varchar(20))
BEGIN


 CREATE TEMPORARY TABLE IF NOT EXISTS TMP_Payment_Listing(PaymentID bigint(20),MLID bigint(20),OwnerID bigint(20),
 PaymentDate datetime,Amount double,Method varchar(20),ReferenceNumber varchar(50),Comments longtext,
 Owner varchar(100),AddedBy varchar(25),AddedOn datetime,LastEditedBy varchar(25),LastEditedOn datetime,flag varchar(1));


 Insert into TMP_Payment_Listing
     SELECT P.Payment_ID, P.MLID, P.customer_ID, P.PaymentDate,P.Amount, P.Method, P.ReferenceNumber,
            P.Comments, FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial),
            P.AddedBy, P.AddedOn, P.LastEditedBy, P.LastEditedOn,'0'
	FROM Payment P
	INNER JOIN customer c ON (P.customer_id = c.customer_id)
        WHERE P.MLID = COALESCE(i_mlid,p.mlid) AND (c.MID = i_mid)
        AND (P.PaymentDate >= CONCAT(fromDate,' 00:00:00'))
        AND (P.PaymentDate <= CONCAT(toDate,' 23:59:59'));

 Insert into TMP_Payment_Listing
       SELECT A.Appointment_ID, A.MLID, -1, A.VisitDate,FuncAppointmentMiscTax(A.Appointment_ID),PM.Method,PM.ReferenceNumber,
        PM.Comments,'-- Misc Transaction --', PM.AddedBy,PM.AddedOn,PM.LastEditedBy,PM.LastEditedOn,'1'
	FROM PaymentMisc PM
	INNER JOIN Appointment A ON (PM.Appointment_ID = A.Appointment_ID)
	INNER JOIN thau_MemberLocal ML ON A.MLID = ML.memberlocal_Id
	INNER JOIN thau_Member M ON (ML.member_id = M.member_id)
	WHERE (M.member_id = i_mid)
        AND (ML.memberlocal_id = COALESCE(i_mlid, ML.memberlocal_Id))
        AND (A.VisitDate >= CONCAT(fromDate,' 00:00:00'))
        AND (A.VisitDate <= CONCAT(toDate,' 23:59:59'));

   SELECT TP.PaymentID,TP.PaymentDate,ML.Clinic,TP.Method,
          TP.ReferenceNumber,TP.Amount,TP.Owner,
          FuncOwnerBalanceAtMLID(TP.OwnerID,null,null) AS Balance,
          FuncOwnerBalanceAtMLID(TP.OwnerID,Date(TP.PaymentDate),null) AS BalanceAsOf,flag
     FROM TMP_Payment_Listing TP
    LEFT JOIN thau_MemberLocal ML ON (TP.MLID = ML.memberlocal_id)
    --  ORDER BY TP.PaymentDate DESC,TP.PaymentID DESC;
    ORDER BY TP.paymentdate, TP.method DESC;

  DROP TEMPORARY table IF EXISTS TMP_Payment_Listing;

END $$

DELIMITER ;
--
-- Definition of procedure `PaymentOverPaid`
--
DELIMITER $$

DROP PROCEDURE IF EXISTS `PaymentOverPaid` $$
CREATE PROCEDURE `PaymentOverPaid`(In i_mlid bigint(20),In i_mid bigint(20),In flag int)
BEGIN

  CREATE TEMPORARY TABLE IF NOT EXISTS TMP (ownerName varchar (50),balanceValue double);

 if(flag = 0)  then
  Insert into TMP
         select FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial),
                FuncOwnerBalanceAtMLID(customer_id,null,i_mlid)
         from customer AS c where c.mid = i_mid and FuncOwnerBalanceAtMLID(customer_id,null,i_mlid) > 0;
  else
  Insert into TMP
         select FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial),
                FuncOwnerBalanceAtMLID(customer_id,null,i_mlid)
         from customer AS c where c.mid = i_mid and FuncOwnerBalanceAtMLID(customer_id,null,i_mlid) < 0;
 end if;

  select distinct ownerName,balanceValue FROM TMP order by ownerName;

  DROP TEMPORARY table IF EXISTS TMP;

END $$

DELIMITER ;
