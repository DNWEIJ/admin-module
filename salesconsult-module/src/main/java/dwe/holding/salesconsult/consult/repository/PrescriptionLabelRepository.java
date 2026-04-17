package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.PrescriptionLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionLabelRepository extends JpaRepository<PrescriptionLabel, Long> {
    Optional<PrescriptionLabel> getPrescriptionLabelByLineItemId(Long lineItemId);
}
