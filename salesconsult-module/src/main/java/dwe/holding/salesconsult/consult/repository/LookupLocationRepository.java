package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.LookupLocation;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupLocationRepository extends JpaRepository<LookupLocation, Long> {

    @Cacheable("locations")
    List<LookupLocation> findByMemberId(Long currentUserMid);
}
