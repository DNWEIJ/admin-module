package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.lookup.LookupBreeds;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LookupBreedsRepository extends JpaRepository<LookupBreeds, Long> {

    @Cacheable("breeds")
    @Query(" SELECT b from LookupBreeds b where b.speciesName = :name ")
    List<LookupBreeds> findBySpeciesName(String name);

    @Cacheable("breeds")
    List<LookupBreeds> findBySpecies_Id(Long id);
}