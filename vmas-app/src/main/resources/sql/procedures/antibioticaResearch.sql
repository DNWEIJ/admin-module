drop table vmas.tmp_appointment_id if exists;
drop table vmas.tmp_patient_id if exists;

create table vmas.tmp_appointment_id( appointment_id bigint(20));
create table vmas.tmp_patient_id( patient_id bigint(20));

INSERT INTO tmp_appointment_id( appointment_id)
select  l.appointment_id from
	appointment ap join lineitem l on ap.appointment_id = l.appointment_id
WHERE '2009-01-01 00:00'  < ap.visitDate 
AND ap.visitDate <= '2011-12-31 : 23:59'
AND ap.mid = 77 and l.mid = 77  
GROUP BY l.appointment_id ;

INSERT INTO vmas.tmp_patient_id( patient_id)
SELECT  v.patient_id from 
	visit v join tmp_appointment_id t on v.appointment_id = t.appointment_id
WHERE   v.mid = 77
GROUP BY v.patient_id ;		

select count(*) from patient p join vmas.tmp_patient_id t on p.patient_id = t.patient_id 
WHERE (lcase(species) = 'canine' or lcase(species) = 'feline')  and mid = 77 ;
