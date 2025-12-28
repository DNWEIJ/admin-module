package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    Optional<Estimate> findByIdAndMemberId(Long estimateId, Long currentUserMid);
}