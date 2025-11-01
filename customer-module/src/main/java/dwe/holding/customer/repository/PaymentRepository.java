package dwe.holding.customer.repository;

import dwe.holding.customer.model.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer_IdOrderByPaymentDateDesc(Long customerId);
}