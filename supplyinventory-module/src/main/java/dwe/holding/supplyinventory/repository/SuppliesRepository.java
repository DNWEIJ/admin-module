package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Supply;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuppliesRepository extends JpaRepository<Supply, Long> {

    List<Supply> findByDistributorNameIgnoreCase(@NotEmpty String distributorName);

    @Query("""
            select s.distributorName from Supply as s WHERE s.memberId = :memberId GROUP BY s.distributorName
            """)
    List<String> findByMemberIdGroupByDistributorName(Long memberId);
}