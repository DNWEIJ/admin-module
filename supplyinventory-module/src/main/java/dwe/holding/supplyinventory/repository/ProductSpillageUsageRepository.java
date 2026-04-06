package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.ProductSpillageUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpillageUsageRepository extends JpaRepository<ProductSpillageUsage, Long> {

}