DELIMITER $$
DROP FUNCTION IF EXISTS `FuncFormatOwnerName` $$
CREATE  FUNCTION `FuncFormatOwnerName`(LastName varchar(20),FirstName varchar(20),Surname varchar(9),MiddleInitial varchar(1)) RETURNS varchar(100) CHARSET latin1
BEGIN
DECLARE TempStr varchar(100);
	SET TempStr = LastName;
	IF (LENGTH(Surname) > 0) THEN
		SET TempStr =  CONCAT(Surname,' ',TempStr);
        END IF;
	IF (LENGTH(FirstName) > 0) THEN
		SET TempStr = CONCAT(TempStr,', ',FirstName);
	END IF;
	IF (LENGTH(MiddleInitial) > 0) THEN
		SET TempStr = CONCAT(TempStr,' ',MiddleInitial,'.');
        END IF;
	RETURN TempStr;
END $$
DELIMITER ;