DELIMITER $$
DROP FUNCTION IF EXISTS `FuncOwnerBalanceAtMLID` $$
CREATE FUNCTION `FuncOwnerBalanceAtMLID`(OwnerId BIGINT(20),IN_TODATE datetime,IN_MLID BIGINT(20)) RETURNS double
BEGIN
DECLARE TempLineItemsV DOUBLE;
DECLARE TempPayments DOUBLE;
DECLARE BalanceAtMLID DOUBLE;
-- Set the selection to maximum time.
if(IN_TODATE is not null) then
    SET IN_TODATE =DATE(IN_TODATE);
   SET IN_TODATE = ADDTIME(IN_TODATE, '23:59');
end if;
-- BalanceAtMLID
if(IN_TODATE is not null) then
      if(IN_MLID is not null) then
       SELECT coalesce(Sum(Amount),0) FROM Payment P WHERE P.customer_id = OwnerId AND P.MLID = IN_MLID AND p.paymentdate <= IN_TODATE into TempPayments;
      else
       SELECT coalesce(Sum(Amount),0) FROM Payment P WHERE P.customer_id = OwnerId AND p.paymentdate <= IN_TODATE into TempPayments;
      end if;
	if(IN_MLID is not null) then
		SELECT coalesce(Sum(LI.Total),0)
		FROM LineItem LI
		INNER JOIN Patient P     ON LI.Patient_ID = P.Patient_ID
		INNER JOIN Appointment A ON LI.Appointment_ID = A.Appointment_ID
		WHERE A.MLID = IN_MLID AND A.VisitDate <= IN_TODATE and P.customer_id = OwnerId  into TempLineItemsV;
	else
		SELECT coalesce(Sum( LI.Total),0)
		FROM LineItem LI
		INNER JOIN Patient P     ON LI.Patient_ID = P.Patient_ID
		INNER JOIN Appointment A ON LI.Appointment_ID = A.Appointment_ID
		WHERE P.customer_id = OwnerId AND A.VisitDate <= IN_TODATE into TempLineItemsV;
	end if;
-- BalanceAsOfAtMLID
else
        if(IN_MLID is not null) then
	     SELECT coalesce(Sum(Amount),0) FROM Payment P WHERE P.customer_id = OwnerId AND P.MLID = IN_MLID into TempPayments;
     	SELECT coalesce(Sum(LI.Total),0)
	     FROM LineItem LI
	     INNER JOIN Patient P     ON LI.Patient_ID = P.Patient_ID
	     INNER JOIN Appointment A ON LI.Appointment_ID = A.Appointment_ID
	     WHERE P.customer_id = OwnerId AND A.MLID = IN_MLID  into TempLineItemsV;
        else
             SELECT coalesce(Sum(Amount),0) FROM Payment P WHERE P.customer_id = OwnerId into TempPayments;
     	SELECT coalesce(Sum(LI.Total),0) FROM LineItem LI INNER JOIN Patient P ON LI.Patient_ID = P.Patient_ID
	     WHERE P.customer_id = OwnerId into TempLineItemsV;
       end if;
end if;
    IF(TempLineItemsV IS NULL) THEN
           SET TempLineItemsV = 0;
     END IF;
     IF(TempPayments IS NULL) THEN
           SET TempPayments = 0;
     END IF;
     SET  BalanceAtMLID =  TempPayments -  TempLineItemsV;
     return Round(BalanceAtMLID,2);
END $$
DELIMITER ;