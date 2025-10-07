package dwe.holding.generic.suppliesandinventory.repository;

import dwe.holding.generic.suppliesandinventory.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DistributorRepository extends JpaRepository<Distributor, UUID> {
    List<Distributor> findByMemberId(UUID id);
}