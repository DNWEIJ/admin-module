package dwe.holding.salesconsult.sales.repository;


import dwe.holding.salesconsult.sales.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
    List <Payment> findMaxPaymentDate(Long memberId, Long customerId);
}