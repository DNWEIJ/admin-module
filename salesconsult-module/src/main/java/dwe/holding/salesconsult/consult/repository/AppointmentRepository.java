package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndMemberId(Long appointmentId, Long MemberId);

    List<Appointment> findByVisitDateTimeBetweenAndLocalMemberId(LocalDateTime start, LocalDateTime end, Long mlid);

    List<Appointment> findByVisitDateTimeBetweenAndOTCAndLocalMemberId(LocalDateTime start, LocalDateTime end, YesNoEnum otc, Long mlid);

    List<Appointment> findByMemberIdAndVisits_Pet_Customer_Id(Long memberId, Long customerId);

    @NativeQuery(value = """
            SELECT
                a.id,
                a.visit_date_time,
                a.cancelled,
                a.completed,
                a.otc,
                v.id,
                v.estimated_time_in_minutes,
                v.veterinarian,
                v.purpose,
                v.room,
                v.status,
                p.id,
                p.name,
                p.species,
                p.breed,
                c.id,
                c.last_name,
                c.first_name,
                c.sur_name,
                c.middle_initial,
                COALESCE(li.total_amount, 0),
                COALESCE(pay.paid_amount, 0)
            FROM consult_appointment a
            STRAIGHT_JOIN consult_visit v
                ON v.appointment_id = a.id
            STRAIGHT_JOIN customer_pet p
                ON v.pet_id = p.id
            STRAIGHT_JOIN customer_customer c
                ON p.customer_id = c.id
            LEFT JOIN (
                SELECT
                    appointment_id,
                    pet_id,
                    SUM(total_inc_tax) total_amount
                FROM sales_line_item
                GROUP BY appointment_id, pet_id
            ) li
                ON li.appointment_id = a.id
               AND li.pet_id = p.id
            LEFT JOIN (
                SELECT
                    pv.visit_id,
                    SUM(sales_payment.amount) paid_amount
                FROM consult_payment_visit pv
                JOIN sales_payment
                    ON sales_payment.id = pv.payment_id
                GROUP BY pv.visit_id
            ) pay
                ON pay.visit_id = v.id
            WHERE a.visit_date_time BETWEEN :fromDate AND :toDate
              AND a.member_id = :mid
              AND a.local_member_id = :mlid
            """
    )
    List<VisitListProjection> findAppointmentOverviewWithMoney(
            @Param("mid") Long mid,
            @Param("mlid") Long mlid,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @NativeQuery(value = """
            SELECT
                a.id,
                a.visit_date_time,
                a.cancelled,
                a.completed,
                a.otc,
                v.id,
                v.estimated_time_in_minutes,
                v.veterinarian,
                v.purpose,
                v.room,
                v.status,
                p.id,
                p.name,
                p.species,
                p.breed,
                c.id,
                c.last_name,
                c.first_name,
                c.sur_name,
                c.middle_initial
            FROM consult_appointment a
            JOIN consult_visit v
                ON v.appointment_id = a.id
            JOIN customer_pet p
                ON v.pet_id = p.id
            JOIN customer_customer c
                ON p.customer_id = c.id
            WHERE a.visit_date_time BETWEEN :fromDate AND :toDate
              AND a.member_id = :mid
              AND a.local_member_id = :mlid
            """
    )
    List<VisitListProjection> findAppointmentOverviewWithoutMoney(
            @Param("mid") Long mid,
            @Param("mlid") Long mlid,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

}