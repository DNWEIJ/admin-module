package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.ProductPricePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ProductPricePromotionRepository extends JpaRepository<ProductPricePromotion, Long> {

    @Query("""
            select case when count(p) > 0 then true else false end
                        from ProductPricePromotion p
                        where p.productId = :productId  
                        and (:promotionId is null or p.id != :promotionId)  
                        and p.startDate <= :endDate  
                        and p.endDate >= :startDate
            """)
    boolean hasOverlappingPromotions(@Param("productId") Long productId, @Param("promotionId") Long promotionId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate
    );

}