package dwe.holding.salesconsult.sales.repository;


import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.projection.PaymentVisitDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer_IdOrderByPaymentDateDesc(Long customerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment as p  WHERE p.customer.id = :customerId and p.paymentDate <= :date and p.memberId = :memberId")
    BigDecimal getSumAmountOfPayment(@Param("customerId") Long customerId, @Param("date") LocalDateTime limitDateTime, @Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment as p WHERE p.customer.id = :customerId and p.memberId = :memberId")
    BigDecimal getSumAmountOfPayment(@Param("customerId") Long customerId, @Param("memberId") Long memberId);


    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment as p WHERE p.customer.id = :customerId")
    BigDecimal getSumAmountOfPayment(@Param("customerId") Long customerId);


    @Query("""
                SELECT sp
                FROM Payment sp
                WHERE sp.customer.id = :customerId AND sp.memberId = :memberId
                  AND sp.paymentDate = (
                      SELECT MAX(sp2.paymentDate)
                      FROM Payment sp2
                      WHERE sp2.customer.id = :customerId AND sp2.memberId = :memberId
                  )
            """)
    List<Payment> findMaxPaymentDate(Long memberId, Long customerId);


    @EntityGraph(
            attributePaths = {"customer", "paymentVisits"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    @Query("""
            
                    SELECT p
            FROM Payment p
            WHERE p.paymentDate BETWEEN :fromDate AND :toDate
            AND p.memberId = :memberId
            AND (:localMemberId = 0 OR p.localMemberId = :localMemberId)
            ORDER BY p.paymentDate ASC
            """)
    List<Payment> findPaymentsWithBalance(
            @Param("memberId") Long memberId,
            @Param("localMemberId") Long localMemberId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT pv.payment.id, v.id, a.id, a.OTC
            FROM PaymentVisit pv
            JOIN pv.visit v
            JOIN v.appointment a
            WHERE pv.payment.id IN :paymentIds
            """)
    List<Object[]> findVisitAndAppointmentId(List<Long> paymentIds);

    @Query("""
            SELECT new dwe.holding.salesconsult.sales.repository.projection.PaymentVisitDTO(
                pay.id,
                pay.amount,
                pay.paymentDate,
                pay.customer.id,
                pv.id,
                pv.visit.id)
            FROM Payment pay
            LEFT JOIN pay.paymentVisits pv
            WHERE pay.customer.id IN :customerIds
            ORDER BY pay.customer.id, pay.paymentDate ASC
            """)
    List<PaymentVisitDTO> findPaymentsForCustomers(List<Long> customerIds);

    @Query("""
           SELECT new dwe.holding.salesconsult.sales.repository.projection.PaymentVisitDTO(
                pay.id,
                pay.amount,
                pay.paymentDate,
                pay.customer.id,
                null,
                null)
            FROM Payment pay
            WHERE pay.paymentVisits IS EMPTY
            """)
    List<PaymentVisitDTO> findMigrationPaymentsNotLinkedForCustomers();

}
