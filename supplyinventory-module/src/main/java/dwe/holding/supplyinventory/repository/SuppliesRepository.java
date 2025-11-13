package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Supplies;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SuppliesRepository extends JpaRepository<Supplies,   Long> {
}