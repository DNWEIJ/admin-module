package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.AnalyseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyseItemRepository extends JpaRepository<AnalyseItem, Long> {
    List<AnalyseItem> findByMemberIdAndAppointmentIdAndPetId(Long memberId, Long appointmentId, Long petId);
}