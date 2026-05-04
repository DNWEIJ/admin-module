package dwe.holding.salesconsult.consult.repository;

import dwe.holding.customer.client.model.lookup.LookupPurpose;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupPurposeRepository extends JpaRepository<LookupPurpose, Long> {
    @Cacheable("purpose")
    List<LookupPurpose> getByMemberIdOrderByDefinedPurpose(Long memberId);
}