package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.client.model.lookup.LookupPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupPurposeRepository extends JpaRepository<LookupPurpose, Long> {
        List<LookupPurpose> getByMemberIdOrderByDefinedPurpose(Long memberId);
    }