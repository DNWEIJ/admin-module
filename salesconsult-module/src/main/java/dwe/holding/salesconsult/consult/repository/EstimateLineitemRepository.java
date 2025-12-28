package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Estimatelineitem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateLineitemRepository extends JpaRepository<Estimatelineitem, Long> {

    List<Estimatelineitem> findByEstimate_IdAndPet_Id(Long estimateId, Long petId);
}