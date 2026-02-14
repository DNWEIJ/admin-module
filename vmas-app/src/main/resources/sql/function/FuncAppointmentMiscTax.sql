DELIMITER $$
DROP FUNCTION IF EXISTS `FuncAppointmentMiscTax` $$
CREATE FUNCTION `FuncAppointmentMiscTax`(appointmentId bigint(20)) RETURNS double
BEGIN
    Declare lineitemmisc_sum DOUBLE;
    SELECT coalesce(sum(total),0) FROM lineitemsmisc where appointment_id = appointmentId into lineitemmisc_sum;
    return Round(lineitemmisc_sum,2);
END $$
DELIMITER ;