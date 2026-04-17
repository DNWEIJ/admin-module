package dwe.holding.supplyinventory.repository;

import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupProductCategory;
import dwe.holding.supplyinventory.model.LookupProductCategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LookupProductCategoryRepository extends JpaRepository<LookupProductCategory, Long> {

    @Query("SELECT new dwe.holding.supplyinventory.model.LookupProductCategoryDto(c, COUNT(p)) " +
            "FROM LookupProductCategory c " +
            "LEFT JOIN Product p ON p.lookupProductCategory = c " +
            "GROUP BY c.categoryName")
    List<LookupProductCategoryDto> findAllWithConnection();

    // TODO Add cache
    Optional<LookupProductCategory> findByIdAndMemberId(Long id, Long currentUserMid);

    @Query("SELECT new dwe.holding.shared.model.frontend.PresentationElement(c.id, c.categoryName) " +
            "FROM LookupProductCategory c WHERE c.deleted = :deleted " +
            "ORDER BY c.categoryName")
    List<PresentationElement> findByDeletedOrderByCategoryName(YesNoEnum deleted);

    @Query("SELECT new dwe.holding.shared.model.frontend.PresentationElement(c.id, c.categoryName) " +
            "FROM LookupProductCategory c "+
            "ORDER BY c.categoryName")
    List<PresentationElement> findByOrderByCategoryName();
}