package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.LookupCostingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LookupCostingCategoryRepository extends JpaRepository<LookupCostingCategory, Long> {
    // TODO Add cache
    Collection<LookupCostingCategory> findByMemberIdInOrderByCategory(List memberIds);
    // TODO Add cache
    Optional<LookupCostingCategory> findByIdAndMemberId(Long id, Long currentUserMid);
}