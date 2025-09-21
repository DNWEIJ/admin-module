package dwe.holding.generic.suppliesandinventory.repository;

import dwe.holding.generic.suppliesandinventory.model.Supplies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliesRepository extends JpaRepository<Supplies, Long> {
}