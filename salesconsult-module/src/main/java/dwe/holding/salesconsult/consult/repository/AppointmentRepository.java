package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndMemberId(Long appointmentId, Long MemberId);

    List<Appointment> findByVisitDateTimeBetweenAndLocalMemberId(LocalDateTime start, LocalDateTime end, Long mlid);

    List<Appointment> findByVisitDateTimeBetweenAndOTCAndLocalMemberId(LocalDateTime start, LocalDateTime end, YesNoEnum otc, Long mlid);

    List<Appointment> findByMemberIdAndVisits_Id(Long memberId, Long visitId);


    @Query(value = """
        SELECT COUNT(DISTINCT v.id) 
        FROM Visit v 
        JOIN v.appointment a 
        JOIN a.lineItems l
        WHERE a.visitDateTime > :from 
        AND a.visitDateTime <= :till 
        AND a.memberId = :memberId
        """)
    Long countVisitsByDateRange(
            @Param("from") LocalDateTime from,
            @Param("till") LocalDateTime till,
            @Param("memberId") Long memberId
    );

    @Query(value = """
        SELECT COUNT(DISTINCT a.id) 
        FROM Appointment a 
        JOIN a.lineItems l
        WHERE a.visitDateTime > :from 
        AND a.visitDateTime <= :till 
        AND a.memberId = :memberId
        """)
    Long countAppointmentsByDateRange(@Param("from") LocalDateTime from, @Param("till") LocalDateTime till, @Param("memberId") Long memberId);
}