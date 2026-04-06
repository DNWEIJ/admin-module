package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.ProductSpillage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpillageRepository extends JpaRepository<ProductSpillage, Long> {

    ProductSpillage findByNameAndMemberIdAndLocalMemberIdAndEndDateNotNull(String name, Long memberId, Long localMemberId);
}