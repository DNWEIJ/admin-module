package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.ProductBatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductBatchNumberRepository extends JpaRepository<ProductBatchNumber, Long> {

    List<ProductBatchNumber> findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNull(Long costingId, Long memberId, Long localMemberId);

    Optional<ProductBatchNumber> findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNullAndBatchNumber(Long costingId, Long memberId, Long localMemberId, String batchNumber);
}