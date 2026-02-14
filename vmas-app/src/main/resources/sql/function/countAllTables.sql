DELIMITER $$

DROP PROCEDURE IF EXISTS countAllTables $$
CREATE PROCEDURE countAllTables()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE var_tablename VARCHAR(255);
  DECLARE CountValue  BigInt(20);
  DECLARE tablenames CURSOR
  FOR
  SELECT table_name FROM information_schema.`TABLES` T where table_schema = 'vmas' and table_name != 'Temp_Counts' order by table_name;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  DROP TABLE IF EXISTS Temp_Counts;
  CREATE TABLE Temp_Counts (tablename varchar(400),counting BigInt(20));

  OPEN tablenames;
  REPEAT
    FETCH tablenames INTO var_tablename;

    SET @s = CONCAT("insert into Temp_Counts (tablename,counting) values ('", var_tablename, "', (SELECT COUNT(*) FROM vmo_dbo.", var_tablename, "));");

  prepare stm from  @s;
  execute stm;
  deallocate prepare stm;
   -- End of loop
   UNTIL done END REPEAT;

   SELECT  * from Temp_Counts;
  DROP TABLE IF EXISTS Temp_Counts;

END $$

DELIMITER ;