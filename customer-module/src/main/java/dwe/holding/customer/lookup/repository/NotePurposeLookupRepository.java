package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.client.model.lookup.LookupNotePurpose;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotePurposeLookupRepository extends JpaRepository<LookupNotePurpose, Long> {
        @Cacheable("notePurpose")
        List<LookupNotePurpose> getByMemberId(Long memberId);
    }