package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.LookupCostingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LookupCostingCategoryRepository extends JpaRepository<LookupCostingCategory, Long> {
    Collection<LookupCostingCategory> findByMemberIdInOrderByCategory(List memberIds);

    Optional<LookupCostingCategory> findByIdAndMemberId(Long id, Long currentUserMid);
}