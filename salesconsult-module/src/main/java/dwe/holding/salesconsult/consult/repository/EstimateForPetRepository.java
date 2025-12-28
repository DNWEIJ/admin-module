package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.EstimateForPet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstimateForPetRepository extends JpaRepository<EstimateForPet, Long> {
    List<EstimateForPet> findByMemberIdAndPet_IdInOrderByEstimate_EstimateDateDesc(Long memberId, List<Long> PetId);

    Optional<EstimateForPet> findByIdAndMemberId(Long estimateForPetId, Long currentUserMid);
}