package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndMemberId(Long appointmentId, Long MemberId);

    List<Appointment> findByVisitDateTimeBetweenAndLocalMemberId(LocalDateTime start, LocalDateTime end, Long mlid);

    List<Appointment> findByVisitDateTimeBetweenAndOTCAndLocalMemberId(LocalDateTime start, LocalDateTime end, YesNoEnum otc, Long mlid);

    List<Appointment> findByMemberIdAndVisits_Pet_Customer_Id(Long memberId, Long customerId);

}