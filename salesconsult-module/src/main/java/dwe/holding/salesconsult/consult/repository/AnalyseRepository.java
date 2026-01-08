package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyseRepository extends JpaRepository<Analyse, Long> {

    List<Analyse> findByMemberId(Long memberId);

    List<Analyse> findByMemberIdAndAnalyseDescription_Id(Long currentUserMid, Long descriptionId);
}