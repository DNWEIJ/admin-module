package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.converter.TaxedTypeEnumConverter;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Table(name = "SUPPLY_PRODUCT")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 * Costing Inventory/Supply
 * Products can be managed for inventory via two ways:
 * 1. Connected to a supply
 * 2. Not being connected to a supply
 *
 * - Connected to a supply will make incoming orders from a supplier easier to sort out
 * - Not being connected is less work and can be done when a different system is used for placing orders at suppliers
 */
public class Product extends MemberBaseBO {

    // PRODUCT SPECIFIC
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasBatchNr;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasSpillage;
    @Column(nullable = false)
    private String nomenclature;
    private String shortCode;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deleted;

    // PRICING
    // calculated price for the amount you paid to the supplier; can be a big with x time this product
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal purchaseDistributorPrice;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal salesPriceExTax;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal processingFeeExTax;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = TaxedTypeEnumConverter.class)
    private TaxedTypeEnum taxed;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal uplift;
    private Long barcode;

    // SUPPLY
    // option 1: do not fill in anything - no inventory tracking
    // Option 2:  manually maintain the information on the product order reference, no tracking of inventory
    private String distributor;
    private String distributorDescription;
    private String itemNumber;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal quantityPerPackage;
    // Option 3:  product order reference and inventory management
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUPPLY_ID", nullable = true)
    private Supply supply;

    @OneToMany(mappedBy = "productId", fetch = FetchType.LAZY)
    private List<ProductPricePromotion> pricePromotions;

    Long supplyIndyQtyDeduction; // pending on this we deduct x times: product can be '5bags' so we need to have 5 here

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum autoReminder; // for eg repeat vaccination
    private String reminderNomenclature;
    private Short intervalInWeeks;
    private String rRemovePendingRemindersContaining;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deceasedPetPrompt;

    private String certificateManufacturer;
    private String certificateType;
    private String certificateSerialNumber;
    private String certificateVaccineExpires;

    @Lob
    private String instructions;
    private String prescriptionLabel;

    // TODO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOOKUP_PRODUCT_CATEGORY_ID", nullable = false)
    private LookupProductCategory lookupProductCategory;

    public BigDecimal getTotalSalesPriceIncTax(BigDecimal taxGoodPercentage, BigDecimal taxServicePercentage){
        return calculateTotal(
                this.salesPriceExTax, this.processingFeeExTax, BigDecimal.ONE, this.taxed, taxGoodPercentage, taxServicePercentage, null);
    }

    // Calculating the total amount for a customer, so including tax
    // This is also called for lineItem; required here so during pricing, we can calculate it as well, no access to Line Item from here
    public static BigDecimal calculateTotal(
            BigDecimal salesPriceExTax, BigDecimal processingFeeExTax, BigDecimal quantity, TaxedTypeEnum taxedTypeEnum,
            BigDecimal taxGoodPercentage, BigDecimal taxServicePercentage, BigDecimal reductionPercentage
    ) {
        BigDecimal hundred = new BigDecimal("100.0");
        // product can be calculated with low or high tax
        BigDecimal useTaxPercentage = BigDecimal.ZERO;

        if (TaxedTypeEnum.GOOD.equals(taxedTypeEnum)) { // low
            useTaxPercentage = taxGoodPercentage.divide(hundred, 4, RoundingMode.HALF_UP);
        }
        if (TaxedTypeEnum.SERVICE.equals(taxedTypeEnum)) { // high
            useTaxPercentage = taxServicePercentage.divide(hundred, 4, RoundingMode.HALF_UP);
        }

        BigDecimal realCost = salesPriceExTax;
        if (reductionPercentage != null) {
            realCost = realCost.multiply(
                    BigDecimal.ONE.subtract(reductionPercentage.divide(hundred, 4, RoundingMode.HALF_UP))
            );
        }

        BigDecimal part1 = realCost.multiply(quantity).multiply(useTaxPercentage.add(BigDecimal.ONE));
        BigDecimal part2 = processingFeeExTax.add(
                // processingFee is ALWAYS Service taxed (high)
                processingFeeExTax.multiply(
                        taxServicePercentage.divide(hundred, 4, RoundingMode.HALF_UP)
                )
        );

        return part1.add(part2);
    }
}