package dwe.holding.generic.app.suppliesandinventory.repository;

import dwe.holding.generic.app.suppliesandinventory.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DistributorRepository extends JpaRepository<Distributor, UUID> {
}