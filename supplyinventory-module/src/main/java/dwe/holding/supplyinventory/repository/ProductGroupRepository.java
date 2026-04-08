package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {

    List<ProductGroup> getCostingGroupsByParentProductId(Long productParentId);
}