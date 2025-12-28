package dwe.holding.salesconsult.sales.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class CostCalc extends MemberBaseBO {

    @NotNull
    private Long categoryId;
    @NotNull
    private Long costingId;

    @NotEmpty
    @Column(nullable = false)
    private String nomenclature;

    @Column(precision = 38, scale = 4)
    private BigDecimal salesPriceExTax;

    @Column(precision = 38, scale = 4)
    private BigDecimal processingFeeExTax;


    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal quantity;

    // TAX part
    @Column(precision = 38, scale = 4)
    private BigDecimal taxPortionOfProduct;

    @Column(precision = 38, scale = 4)
    private BigDecimal taxPortionOfProcessingFeeService;

    @NotNull
    private TaxedTypeEnum taxedTypeEnum;

    @Column(precision = 38, scale = 4)
    private BigDecimal taxGoodPercentage;

    @Column(precision = 38, scale = 4)
    private BigDecimal taxServicePercentage;

    @Column(precision = 38, scale = 4)
    private BigDecimal totalIncTax;

    @Transient
    public BigDecimal calculateTotal(BigDecimal reduction) {
        BigDecimal hundred = new BigDecimal("100.0");
        BigDecimal goodTax = new BigDecimal("0.0");

        if (TaxedTypeEnum.GOOD.equals(taxedTypeEnum)) {
            goodTax = getTaxGoodPercentage().divide(hundred, 4, RoundingMode.HALF_UP);
        }
        if (TaxedTypeEnum.SERVICE.equals(taxedTypeEnum)) {
            goodTax = getTaxServicePercentage().divide(hundred, 4, RoundingMode.HALF_UP);
        }

        BigDecimal realCost = salesPriceExTax; //real price
        if (reduction != null) {
            //  realCost = realCost * (1 - reduction / 100);
            realCost = realCost.multiply(
                    ((BigDecimal.ONE).subtract(
                            reduction.divide(hundred, 4, RoundingMode.HALF_UP))
                    )
            );
        }

        //  result = ( (realCost * quantity * (goodTtax + 1)) + (processingFeeExTax + processingFeeExTax * (taxServicePercentage / 100.0)) );
        BigDecimal part1 = realCost
                .multiply(quantity)
                .multiply(goodTax.add(BigDecimal.ONE));
        // processingFeeExTax + processingFeeExTax * (taxServicePercentage / 100)
        BigDecimal part2 =
                processingFeeExTax
                        .add(
                                processingFeeExTax.multiply(
                                        taxServicePercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                                )
                        );
        // final result
        return part1.add(part2);
    }

    @Transient
    public BigDecimal calculateProcessingFeeServiceTax() {
        // double result = processingFeeExTax * getTaxServicePercentage() / 100.0;
        return processingFeeExTax.multiply(getTaxServicePercentage()).divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal calculateCostTaxPortion() {
        // double result = getTotal() - (processingFeeExTax + TaxPortionOfProcessingFeeExTaxService + (getQuantity() * salesPriceExtax));
        BigDecimal part1 = quantity.multiply(salesPriceExTax);
        BigDecimal part2 = processingFeeExTax.add(taxPortionOfProcessingFeeService).add(part1);
        return getTotalIncTax().subtract(part2);

    }
}