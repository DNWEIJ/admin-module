package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Table(name = "SUPPLY_SUPPLY",
        indexes = @Index(name = "idx_supply_distributor_name", columnList = "member_id, distributorName")
)
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supply extends MemberBaseBO {
    @Column(nullable = false, length = 40)
    private String nomenclature;
    private BigDecimal quantityPerPackage;
    private BigDecimal minQuantityForAlert;
    private BigDecimal buyQuantityPerOrder;
    private BigDecimal price;
    private String itemNumber;
    private String descriptionOfDistributor;
    private Long barcode;
    private String distributorName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "distributor_id")
    private Distributor distributor;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "supply")
    private List<SupplyLocal> supplyLocals;

    //     private Set<Supplies2localsupdated> updateRecords = new HashSet<Supplies2localsupdated>(0);
}
// TODO  TO ensure we do not create crap, we should have the link via ID not via string
// TODO that would be to distributor
//SELECT
//x.distributor,
//GROUP_CONCAT(DISTINCT x.source ORDER BY x.source SEPARATOR ',') AS sources
//FROM (
//        SELECT distributor, 'costing' AS source FROM vmas.costing
//                UNION ALL
//                SELECT distributor, 'supplies2' AS source FROM vmas.supplies2
//) x
//WHERE NOT EXISTS (
//        SELECT 1
//                FROM vmas.distributor d
//                WHERE d.distributor = x.distributor
//)
//GROUP BY x.distributor;