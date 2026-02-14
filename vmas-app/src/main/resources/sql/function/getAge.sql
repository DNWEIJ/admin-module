DELIMITER $$
DROP FUNCTION IF EXISTS `getAge` $$
CREATE FUNCTION getage(pdate DATE) RETURNS CHAR(30) DETERMINISTIC
BEGIN
DECLARE years INT;
DECLARE months INT;
DECLARE days INT;
 
DECLARE current_year INT;
DECLARE current_month INT;
DECLARE current_day INT;
 
DECLARE year_diff INT;
DECLARE month_diff INT;
DECLARE day_diff INT;
 
DECLARE yearstring CHAR(6);
DECLARE monthstring CHAR(7);
 
SELECT YEAR(pdate) INTO years;
SELECT MONTH(pdate) INTO months;
SELECT DAY(pdate) INTO days;
 
SELECT YEAR(CURRENT_DATE()) INTO current_year;
SELECT MONTH(CURRENT_DATE()) INTO current_month;
SELECT DAY(CURRENT_DATE()) INTO current_day;
 
SELECT (current_year - years) INTO year_diff;
SELECT (current_month - months) INTO month_diff;
SELECT (current_day - days) INTO day_diff;
 
if (current_month < months) THEN
	SET month_diff = (((months - 12) * -1) + current_month);
	SET year_diff = (year_diff - 1);
END IF;
 
if ( month_diff = 1 ) THEN
	SET monthstring = "maand";
ELSE
	SET monthstring = "maanden";
END IF;
 
if ( year_diff = 1 ) THEN
	SET yearstring = "jaar";
ELSE
	SET yearstring = "jaren";
END IF;
 
if ( year_diff = 0 ) THEN
	RETURN CONCAT_WS(' ', month_diff, monthstring);
ELSE
	if (month_diff > 0) THEN
		RETURN CONCAT_WS(' ',year_diff, yearstring, month_diff, monthstring);
	ELSE
		RETURN CONCAT_WS(' ',year_diff, yearstring);
	END IF;
END IF;
 
END $$
 
DELIMITER ;