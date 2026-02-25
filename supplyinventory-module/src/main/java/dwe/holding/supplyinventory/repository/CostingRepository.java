package dwe.holding.supplyinventory.repository;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.projection.CostingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<CostingProjection> findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclature(Long lookupId, Long memberId);

    List<CostingProjection> findByReminderNomenclatureIsNotNullAndReminderNomenclatureIsNotEmptyAndMemberIdOrderByReminderNomenclature(Long memberId);

    @Query("""
                select DISTINCT c.reminderNomenclature as reminderText
                from Costing c
                where c.reminderNomenclature is not null
                  and c.reminderNomenclature <> ''
                  and c.deleted = YesNoEnum.Yes
                  and c.memberId = :memberId
               order by reminderText
            """)
    List<String> findWithNonEmptyReminderNomenclature(Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.CostingDto(
                c,
                case when exists ( select 1 from CostingGroup cg where cg.parentCostingId = c.id )
                    then true
                    else false 
                end
            )
            from Costing c
            where c.id = :id and c.memberId = :memberId
            """)
    CostingDto findByIdAndMemberIdToDto(Long id, Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.CostingDto(
                c,
                case when exists ( select 1 from CostingGroup cg where cg.parentCostingId = c.id )
                    then true
                    else false 
                end
            )
            from Costing c
            where c.lookupCostingCategory.id = :lookupId and c.memberId = :memberId
            order by c.nomenclature
            """)
    List<CostingDto> findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclatureToDto(Long lookupId, Long memberId);

}