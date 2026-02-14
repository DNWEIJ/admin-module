package dwe.holding.salesconsult.sales.repository;


import dwe.holding.salesconsult.sales.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer_IdOrderByPaymentDateDesc(Long customerId);

    @Query("SELECT sum(p.amount) FROM Payment as p  WHERE p.customer.id = :customerId and p.paymentDate <= :date and p.memberId = :memberId")
    BigDecimal getSumAmountOfPayment(@Param("customerId") Long customerId, @Param("date") LocalDateTime limitDateTime, @Param("memberId") Long memberId);

    @Query("SELECT sum(p.amount) FROM Payment as p WHERE p.customer.id = :customerId and p.memberId = :memberId")
    BigDecimal getSumAmountOfPayment(@Param("customerId") Long customerId, @Param("memberId") Long memberId);



    @Query("SELECT max(p.paymentDate) FROM Payment as p WHERE p.customer.id = :customerId and p.memberId = :memberId")
    LocalDate getLatestPaymentDate(@Param("customerId") Long customerId, @Param("memberId") Long memberId);
}