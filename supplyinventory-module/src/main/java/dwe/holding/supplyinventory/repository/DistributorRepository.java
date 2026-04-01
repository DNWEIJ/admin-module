package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Distributor;
import dwe.holding.supplyinventory.model.projection.DistributorProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DistributorRepository extends JpaRepository<Distributor,   Long> {
     List<DistributorProjection> findByMemberId(Long id);

}