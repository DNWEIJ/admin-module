package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.client.model.lookup.LookupSpecies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeciesLookupRepository extends JpaRepository<LookupSpecies, Long> {
    List<LookupSpecies> getByMemberId(Long memberId);
}