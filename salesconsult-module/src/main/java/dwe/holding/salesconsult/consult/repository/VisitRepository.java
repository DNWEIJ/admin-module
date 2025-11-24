package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<VisitProjection> findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(Long memberId, List<Long> patientId);

    Visit findByMemberIdAndId(Long memberId, Long visitId);
}