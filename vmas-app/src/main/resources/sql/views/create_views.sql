CREATE OR REPLACE VIEW V_APPOINTMENT_VISIT  AS 
    select
        appointment.appointment_id  as APPOINTMENT_ID,
        appointment.mlid            as MLID,
        visit.visit_id              as VISIT_ID,
        visit.estimatedtime         as ESTIMATEDTIME,
        appointment.visitdate  as VISITDATE,
        appointment.cancelled  as CANCELLED,
        appointment.completed  as COMPLETED,
        appointment.OTC        as OTC,        
		visit.veterinarian     as VETERINARIAN,
		visit.purpose          as PURPOSE,
		visit.room             as ROOM,
		visit.mid              as MID,
		visit.status           as status,
		patient.petname        as PETNAME,
		patient.species        as SPECIES,
		patient.breed          as BREED,
		patient.patient_id     as PATIENT_ID,
		customer.firstname     as FIRSTNAME, 
		customer.lastname      as LASTNAME,
		customer.surname       as SURNAME,
		customer.customer_id   as CUSTOMER_ID,
		customer.middleinitial as MIDDLEINITIAL,
		0 as AMOUNT,
		0 as PAID
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
            on patient.CUSTOMER_ID=customer.CUSTOMER_ID;
            
            
CREATE OR REPLACE VIEW V_APPOINTMENT_VISIT_AMOUNT  AS 
    select
        V_APPOINTMENT_VISIT.appointment_id  as APPOINTMENT_ID,
        V_APPOINTMENT_VISIT.mlid            as MLID,
        V_APPOINTMENT_VISIT.visit_id              as VISIT_ID,
        V_APPOINTMENT_VISIT.estimatedtime         as ESTIMATEDTIME,
        V_APPOINTMENT_VISIT.visitdate  as VISITDATE,
        V_APPOINTMENT_VISIT.cancelled  as CANCELLED,
        V_APPOINTMENT_VISIT.completed  as COMPLETED,
        V_APPOINTMENT_VISIT.OTC        as OTC,        
		V_APPOINTMENT_VISIT.veterinarian     as VETERINARIAN,
		V_APPOINTMENT_VISIT.purpose          as PURPOSE,
		V_APPOINTMENT_VISIT.room             as ROOM,
		V_APPOINTMENT_VISIT.mid              as MID,
		V_APPOINTMENT_VISIT.status           as status,
		V_APPOINTMENT_VISIT.petname        as PETNAME,
		V_APPOINTMENT_VISIT.species        as SPECIES,
		V_APPOINTMENT_VISIT.breed          as BREED,
		V_APPOINTMENT_VISIT.firstname     as FIRSTNAME, 
		V_APPOINTMENT_VISIT.lastname      as LASTNAME,
		V_APPOINTMENT_VISIT.surname       as SURNAME,
		V_APPOINTMENT_VISIT.customer_id   as CUSTOMER_ID,
		V_APPOINTMENT_VISIT.middleinitial as MIDDLEINITIAL,
		-- add the amount of the consult
		total 
		-- make a join between payment and paymentvisit and visit for the paid amount
		-- ( select sum(amount) from paymentvisit pv 
		-- join payment p on ( `pv`.`payment_id` =  `p`.`payment_id`)
		--   join visit vst on (vst.visit_id = pv.visit_id) 
		--   where vst.visit_id = V_APPOINTMENT_VISIT.visit_id) as paid
    from
    V_APPOINTMENT_VISIT left join lineitem on (lineitem.appointment_id = V_APPOINTMENT_VISIT.appointment_id and lineitem.patient_id = V_APPOINTMENT_VISIT.patient_id);
                   