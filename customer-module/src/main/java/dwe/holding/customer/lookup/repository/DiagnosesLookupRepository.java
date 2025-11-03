package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.client.model.lookup.LookupDiagnose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosesLookupRepository extends JpaRepository<LookupDiagnose, Long> {
    List<LookupDiagnose> getByMemberId(Long memberId);
}