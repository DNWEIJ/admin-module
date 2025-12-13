package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.lookup.LookupBreeds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface LookupBreedsRepository extends JpaRepository<LookupBreeds, Long> {
    List<LookupBreeds> findByMemberIdIn(Collection<Long> memberIds);


}