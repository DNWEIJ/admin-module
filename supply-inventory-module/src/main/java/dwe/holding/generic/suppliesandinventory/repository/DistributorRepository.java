package dwe.holding.generic.suppliesandinventory.repository;

import dwe.holding.generic.suppliesandinventory.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
  

public interface DistributorRepository extends JpaRepository<Distributor,   Long> {
    List<Distributor> findByMemberId(  Long id);
}