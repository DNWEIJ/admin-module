package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Supplies2
 */

@Table(name = "SUPPLY_SUPPLIES")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplies extends MemberBaseBO {

    /**
     * Description of the product
     */
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
     * When we order this product, what is the houseNumber to order? This is the package from the distributor.
     * So multiply with quantityPerPackage to know how many items for sale are there.
     */
    private Double buyQuantity;
    private Double price;
    /**
     * The houseNumber used by the distributor to identify the product.
     */
    private String itemNumber;
    /**
     * The description used by the distributor to identify the product.
     */
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "distributor_id")
    private Distributor distributor;

    private Long barcode;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "supplies")
    private Set<SuppliesLocal> suppliesLocals;

    //     private Set<Supplies2localsupdated> updateRecords = new HashSet<Supplies2localsupdated>(0);
}