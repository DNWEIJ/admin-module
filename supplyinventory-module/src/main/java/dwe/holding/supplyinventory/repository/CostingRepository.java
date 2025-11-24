package dwe.holding.supplyinventory.repository;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.projection.CostingProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CostingRepository extends JpaRepository<Costing, Long> {

    Optional<Costing> findByIdAndMemberId(Long id, Long MemberId);

    // numeric value
    default List<CostingProjection> getCostingProjectionsWhenSearchCriteriaIsNumeric(String searchString, Long memberId) {
        return findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long.valueOf(searchString), searchString, searchString, memberId);
    }

    List<CostingProjection> findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long searchStringOne, String searchStringTwo, String searchStringThee, Long memberId);

    default List<CostingProjection> getCostingOnNomenclature(String searchString, Long memberId) {
        return findByNomenclatureContainsAndMemberIdAndDeleted(searchString, memberId, YesNoEnum.No);
    }

    List<CostingProjection> findByNomenclatureContainsAndMemberIdAndDeleted(String searchString, Long memberId, YesNoEnum no);

    List<CostingProjection> findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclature(Long lookupId, Long currentUserMid);
}