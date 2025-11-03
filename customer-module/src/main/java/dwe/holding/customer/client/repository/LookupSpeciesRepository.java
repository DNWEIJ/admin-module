package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.lookup.LookupSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LookupSpeciesRepository extends JpaRepository<LookupSpecies, Long> {
    @Query("SELECT s FROM CUSTOMER_LOOKUP_SPECIES s WHERE s.memberId = ?1 OR s.memberId = -1")
    List<LookupSpecies> getList(Long memberId);
}