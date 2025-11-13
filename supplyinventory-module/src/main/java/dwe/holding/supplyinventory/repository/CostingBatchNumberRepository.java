package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.CostingBatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CostingBatchNumberRepository extends JpaRepository<CostingBatchNumber, Long> {

    List<CostingBatchNumber> findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNull(Long costingId, Long memberId, Long localMemberId);

    Optional<CostingBatchNumber> findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNullAndBatchNumber(Long costingId, Long memberId, Long localMemberId, String batchNumber);
}