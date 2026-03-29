package dwe.holding.salesconsult.sales.repository;

import dwe.holding.salesconsult.sales.model.Refund;
import dwe.holding.salesconsult.sales.repository.projection.RefundProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
     List<Refund> findByRefundDateBetween(LocalDate from, LocalDate includeTill);

    @Query("""
                SELECT new dwe.holding.salesconsult.sales.repository.projection.RefundProjection(
                    r.id, c.id,
                    c.lastName,c.firstName, c.surName, c.middleInitial,
                    r.refundDate, r.amount, r.comments, r.localMemberId)
                FROM Refund r 
                JOIN Customer c ON r.customerId = c.id
                WHERE c.id = :customerId
            """)
    List<RefundProjection> findByCustomerId(Long customerId);

    Optional<Refund> findByIdAndCustomerId(Long refundId, Long customerId);
}
