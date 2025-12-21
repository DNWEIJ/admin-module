package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndMemberId(Long appointmentId, Long MemberId);
}