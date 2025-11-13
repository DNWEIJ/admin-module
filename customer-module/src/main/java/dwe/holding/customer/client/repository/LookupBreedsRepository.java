package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.lookup.LookupBreeds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LookupBreedsRepository extends JpaRepository<LookupBreeds, Long> {
    @Query("SELECT s FROM CUSTOMER_LOOKUP_BREEDS s WHERE s.memberId = ?1 OR s.memberId = -1")
    List<LookupBreeds> getList(Long memberId);
}