package dwe.holding.salesconsult.sales.model;


import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

@Table(name = "SALES_LINE_ITEM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LineItem extends TenantBaseBO {
    @NotNull
    private Long appointmentId;

    @NotNull
    private Long petId;

    @NotNull
    private Long categoryId;

    @NotEmpty
    @Column(nullable = false)
    private String nomenclature;

    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal quantity;

    @NotNull
    private TaxedTypeEnum taxForSellExTaxPrice;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal taxGoodPercentage;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal taxServicePercentage;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal sellExTaxPrice;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal processingFee;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal total;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal taxPortionOfSell;
    @Column(precision = 38, scale = 4)
    private
    BigDecimal taxPortionOfProcessingFeeService;
    private boolean hasPrintLabel;

    @Transient
    public BigDecimal calculateTotal(BigDecimal reduction) {
        BigDecimal goodTax = new BigDecimal("0.0");

        if (TaxedTypeEnum.GOOD.equals(taxForSellExTaxPrice)) {
            goodTax = getTaxGoodPercentage().divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
        }
        if (TaxedTypeEnum.SERVICE.equals(taxForSellExTaxPrice)) {
            goodTax = getTaxServicePercentage().divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
        }

        BigDecimal realCost = sellExTaxPrice; //real price
        if (reduction != null) {
            realCost = realCost.multiply(
                    ((new BigDecimal("1.00")).min(
                            reduction.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP))
                    )
            );
        }

        //  total = (realCost * quantity * (goodTtax + 1)) + (processingFee + processingFee * (taxServicePercentage / 100.0));
        BigDecimal part1 = realCost
                .multiply(quantity)
                .multiply(goodTax.add(BigDecimal.ONE));
        // processingFee + processingFee * (taxServicePercentage / 100)
        BigDecimal part2 =
                processingFee
                        .add(
                                processingFee.multiply(
                                        taxServicePercentage.divide(BigDecimal.valueOf(100))
                                )
                        );
        // final result
        return part1.add(part2);
    }

    @Transient
    public BigDecimal calculateProcessingFeeServiceTax() {
        return processingFee.multiply(getTaxServicePercentage().divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP));
    }

    @Transient
    public BigDecimal calculateCostTaxPortion() {
        BigDecimal part1 = quantity.multiply(sellExTaxPrice);
        return total.min(processingFee.add(taxPortionOfProcessingFeeService.add(part1)));

    }
}