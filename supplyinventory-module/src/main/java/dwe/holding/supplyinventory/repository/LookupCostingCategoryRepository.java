package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.LookupCostingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface LookupCostingCategoryRepository extends JpaRepository<LookupCostingCategory, Long> {

    @Query("select c from SUPPLY_LOOKUP_COSTING_CATEGORY c WHERE c.memberId = :memberId or c.memberId = -1 order by c.category")
    Collection<LookupCostingCategory> findByMemberIdOrderByCategory(long memberId);
}