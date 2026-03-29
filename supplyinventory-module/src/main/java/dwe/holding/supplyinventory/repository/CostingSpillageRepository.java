package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.CostingSpillage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostingSpillageRepository extends JpaRepository<CostingSpillage, Long> {

    CostingSpillage findByNameAndMemberIdAndLocalMemberIdAndEndDateNotNull(String name, Long memberId, Long localMemberId);
}