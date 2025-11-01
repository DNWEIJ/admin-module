package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.model.lookup.LookupNotePurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupNotePurposeRepository extends JpaRepository<LookupNotePurpose, Long> {
        List<LookupNotePurpose> getByMemberId(Long memberId);
    }