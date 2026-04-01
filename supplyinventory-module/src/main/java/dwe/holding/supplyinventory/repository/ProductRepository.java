package dwe.holding.supplyinventory.repository;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Costing, Long> {

    Optional<Costing> findByIdAndMemberId(Long id, Long MemberId);

    // numeric value
    default List<dwe.holding.supplyinventory.model.projection.CostingProjection> getCostingProjectionsWhenSearchCriteriaIsNumeric(String searchString, Long memberId) {
        return findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long.valueOf(searchString), searchString, searchString, memberId);
    }

    List<dwe.holding.supplyinventory.model.projection.CostingProjection> findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long searchStringOne, String searchStringTwo, String searchStringThee, Long memberId);

    default List<dwe.holding.supplyinventory.model.projection.CostingProjection> getCostingOnNomenclature(String searchString, Long memberId) {
        return findByNomenclatureContainsAndMemberIdAndDeletedOrShortCodeAndMemberIdAndDeleted(searchString, memberId, YesNoEnum.No, searchString, memberId, YesNoEnum.No);
    }

    List<dwe.holding.supplyinventory.model.projection.CostingProjection> findByNomenclatureContainsAndMemberIdAndDeletedOrShortCodeAndMemberIdAndDeleted(String searchString, Long memberId, YesNoEnum no, String searchString1, Long memberId1, YesNoEnum no1);

    List<dwe.holding.supplyinventory.model.projection.CostingProjection> findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclature(Long lookupId, Long memberId);

    @Query("""
                select DISTINCT c.reminderNomenclature as reminderText
                from Costing c
                where c.reminderNomenclature is not null
                  and c.reminderNomenclature <> ''
                  and c.deleted = dwe.holding.shared.model.type.YesNoEnum.Yes
                  and c.memberId = :memberId
               order by reminderText
            """)
    List<String> findWithNonEmptyReminderNomenclature(Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.CostingProjection(
                c,
                c.supply.id,
                case when exists ( select 1 from CostingGroup cg where cg.parentCostingId = c.id )
                    then true
                    else false
                end
            )
            from Costing c
            where c.id = :id and c.memberId = :memberId
            """)
    CostingProjection findByIdAndMemberIdToDto(Long id, Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.CostingProjection(
                c,
                c.supply.id,
                case when exists ( select 1 from CostingGroup cg where cg.parentCostingId = c.id )
                    then true
                    else false 
                end
            )
            from Costing c
            where c.lookupCostingCategory.id = :lookupId and c.memberId = :memberId
            order by c.nomenclature
            """)
    List<CostingProjection> findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclatureToDto(Long lookupId, Long memberId);

}