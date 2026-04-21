package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.EstimateLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateLineitemRepository extends JpaRepository<EstimateLineItem, Long> {

    List<EstimateLineItem> findByEstimate_IdAndPet_Id(Long estimateId, Long petId);
}