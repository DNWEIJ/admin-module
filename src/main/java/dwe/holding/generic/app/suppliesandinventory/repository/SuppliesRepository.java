package dwe.holding.generic.app.suppliesandinventory.repository;

import dwe.holding.generic.app.suppliesandinventory.model.Supplies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SuppliesRepository extends JpaRepository<Supplies, UUID> {
}