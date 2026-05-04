package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.LookupDiagnose;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupDiagnosesRepository extends JpaRepository<LookupDiagnose, Long> {
    @Cacheable("purposes")
    @Nullable List<LookupDiagnose> findByMemberId(Long memberId);
}