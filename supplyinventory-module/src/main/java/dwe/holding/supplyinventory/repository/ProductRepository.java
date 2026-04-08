package dwe.holding.supplyinventory.repository;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndMemberId(Long id, Long MemberId);

    // numeric value
    default List<dwe.holding.supplyinventory.model.projection.ProductProjection> getProductProjectionsWhenSearchCriteriaIsNumeric(String searchString, Long memberId) {
        return findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long.valueOf(searchString), searchString, searchString, memberId);
    }

    List<dwe.holding.supplyinventory.model.projection.ProductProjection> findByBarcodeOrShortCodeStartsWithOrNomenclatureContainsAndMemberId(Long searchStringOne, String searchStringTwo, String searchStringThee, Long memberId);

    default List<dwe.holding.supplyinventory.model.projection.ProductProjection> getProductOnNomenclature(String searchString, Long memberId) {
        return findByNomenclatureContainsAndMemberIdAndDeletedOrShortCodeAndMemberIdAndDeleted(searchString, memberId, YesNoEnum.No, searchString, memberId, YesNoEnum.No);
    }

    List<dwe.holding.supplyinventory.model.projection.ProductProjection> findByNomenclatureContainsAndMemberIdAndDeletedOrShortCodeAndMemberIdAndDeleted(String searchString, Long memberId, YesNoEnum no, String searchString1, Long memberId1, YesNoEnum no1);

    List<dwe.holding.supplyinventory.model.projection.ProductProjection> findAllByLookupProductCategory_IdAndMemberIdOrderByNomenclature(Long lookupId, Long memberId);

    @Query("""
                select DISTINCT c.reminderNomenclature as reminderText
                from Product c
                where c.reminderNomenclature is not null
                  and c.reminderNomenclature <> ''
                  and c.deleted = dwe.holding.shared.model.type.YesNoEnum.Yes
                  and c.memberId = :memberId
               order by reminderText
            """)
    List<String> findWithNonEmptyReminderNomenclature(Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.ProductProjection(
                c,
                c.supply.id,
                case when exists ( select 1 from ProductGroup cg where cg.parentProductId = c.id )
                    then true
                    else false
                end,
                case when exists ( select 1 from ProductPricePromotion cg where cg.productId = c.id )
                    then true
                    else false
                end
            )
            from Product c
            where c.id = :id and c.memberId = :memberId
            """)
    ProductProjection findByIdAndMemberIdToDto(Long id, Long memberId);

    @Query("""
            select new dwe.holding.supplyinventory.repository.ProductProjection(
                c,
                c.supply.id,
                case when exists ( select 1 from ProductGroup cg where cg.parentProductId = c.id )
                    then true
                    else false
                end,
                case when exists ( select 1 from ProductPricePromotion cg where cg.productId = c.id )
                    then true
                    else false
                end
            )
            from Product c
            where c.lookupProductCategory.id = :lookupId and c.memberId = :memberId
            order by c.nomenclature
            """)
    List<ProductProjection> findAllByLookupProductCategory_IdAndMemberIdOrderByNomenclatureToDto(Long lookupId, Long memberId);

}