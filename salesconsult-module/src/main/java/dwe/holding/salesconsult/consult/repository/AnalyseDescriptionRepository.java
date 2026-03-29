package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.AnalyseDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalyseDescriptionRepository extends JpaRepository<AnalyseDescription, Long> {

        List<AnalyseDescription> findByMemberId(Long memberId);

    Optional<AnalyseDescription> findByIdAndMemberId(Long id, Long currentUserMid);
}