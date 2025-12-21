package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.AnalyseDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyseDescriptionRepository extends JpaRepository<AnalyseDescription, Long> {

        List<AnalyseDescription> findByMemberId(Long memberId);
}