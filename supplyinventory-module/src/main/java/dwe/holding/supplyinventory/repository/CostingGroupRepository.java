package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.CostingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostingGroupRepository extends JpaRepository<CostingGroup, Long> {

    List<CostingGroup> getCostingGroupsByParentCostingId(Long costingParentId);
}