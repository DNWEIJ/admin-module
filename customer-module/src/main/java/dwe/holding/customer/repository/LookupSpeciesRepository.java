package dwe.holding.customer.repository;

import dwe.holding.customer.model.lookup.LookupSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LookupSpeciesRepository extends JpaRepository<LookupSpecies, Long> {
    @Query("SELECT s FROM CUSTOMER_LOOKUPSPECIES s WHERE s.memberId = ?1 OR s.memberId = -1")
    List<LookupSpecies> getList(Long memberId);
}