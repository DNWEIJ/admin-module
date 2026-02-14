DELIMITER $$
DROP FUNCTION IF EXISTS `FuncOwnerChargesDuring` $$
CREATE  FUNCTION `FuncOwnerChargesDuring`(ownerId BIGINT(20),fromDate varchar(10),toDate varchar(10)) RETURNS double
BEGIN
DECLARE TempLineItemsV DOUBLE;
	SELECT Sum(R.Total)
		FROM
		(
		SELECT DISTINCT LI.LineItem_ID, LI.Total
			FROM LineItem LI
                                INNER JOIN Visit V ON (LI.Patient_ID = V.Patient_ID) AND (LI.Appointment_ID = V.Appointment_ID)
				INNER JOIN Patient P ON (V.Patient_ID = P.Patient_ID)
				INNER JOIN Appointment A ON (LI.Appointment_ID = A.Appointment_ID)
			WHERE (P.Customer_ID = ownerId)
				AND (A.VisitDate >= CONCAT(fromDate,' 00:00:00'))
			        AND (A.VisitDate <= CONCAT(toDate,' 23:59:59'))
		) R  INTO TempLineItemsV;
          IF(TempLineItemsV IS NULL) THEN
                 SET TempLineItemsV = 0.00;
          END IF;
return ROUND(TempLineItemsV,2);
END $$
DELIMITER ;