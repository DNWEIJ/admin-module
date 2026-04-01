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

@Table(name = "SUPPLY_COSTING")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Costing extends MemberBaseBO {
    // TODO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOOKUPCOSTINGCATEGORY_ID", nullable = false)
    private LookupCostingCategory lookupCostingCategory;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal purchaseDistributorPrice;

    @Column(nullable = false)
    private String nomenclature;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal salesPriceExTax;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal processingFeeExTax;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = TaxedTypeEnumConverter.class)
    private TaxedTypeEnum taxed;

    private String distributor;
    private String distributorDescription;
    private String itemNumber;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal quantityPerPackage;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal uplift;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasBatchNr;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasSpillage;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum autoReminder;

    private String reminderNomenclature;
    private Short intervalInWeeks;
    private String rRemovePendingRemindersContaining;

    private String shortCode;
    private Long barcode;

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

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUPPLY_ID", nullable = true)
    private Supply supply;
    Long supplyIndyQtyDeduction;

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