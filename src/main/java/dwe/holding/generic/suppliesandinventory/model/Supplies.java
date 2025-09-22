package dwe.holding.generic.suppliesandinventory.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.HashSet;
import java.util.Set;

/**
 * Supplies2
 */

@Table(name = "SUPPLIES")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplies extends TenantBaseBO {
    @Column(nullable = false, length = 40)
    private String nomenclature;
    /**
     * Bought from the distributor, what is the size of the package delivered?
     */
    private Double quantityPerPackage;
    /**
     * What is the minimum quantity we would like to have on stock, per location?
     */
    private Double minQuantity;
    /**
     * When we order this product, what is the number to order? This is the package from the distributor.
     * So multiply with quantityPerPackage to know how many items for sale are there.
     */
    private Double buyQuantity;
    private Double price;
    /**
     * The number used by the distributor to identify the product.
     */
    private String itemNumber;
    /**
     * The description used by the distributor to identify the product.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    @Builder.Default
    private Distributor distributor = new Distributor();

    private Long barcode;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "supplies")
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Builder.Default
    private Set<SuppliesLocal> suppliesLocals = new HashSet<SuppliesLocal>(0);
//     private Set<Supplies2localsupdated> updateRecords = new HashSet<Supplies2localsupdated>(0);
}