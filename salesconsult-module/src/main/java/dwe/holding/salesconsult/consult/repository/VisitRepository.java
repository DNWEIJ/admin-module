package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<VisitProjection> findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(Long memberId, List<Long> petId);

    Optional<Visit> findByMemberIdAndId(Long memberId, Long visitId);
    List<Visit> findByMemberIdAndPet_Id(Long memberId, Long petId);
}