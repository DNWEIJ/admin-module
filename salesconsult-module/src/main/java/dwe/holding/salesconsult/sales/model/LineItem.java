package dwe.holding.salesconsult.sales.model;


import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

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
    private TaxedTypeEnum taxedTypeEnum;
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
        BigDecimal hundred = new BigDecimal("100.0");
        BigDecimal goodTax = new BigDecimal("0.0");

        if (TaxedTypeEnum.GOOD.equals(taxedTypeEnum)) {
            goodTax = getTaxGoodPercentage().divide(hundred, 4, RoundingMode.HALF_UP);
        }
        if (TaxedTypeEnum.SERVICE.equals(taxedTypeEnum)) {
            goodTax = getTaxServicePercentage().divide(hundred, 4, RoundingMode.HALF_UP);
        }

        BigDecimal realCost = sellExTaxPrice; //real price
        if (reduction != null) {
            //  realCost = realCost * (1 - reduction / 100);
            realCost = realCost.multiply(
                    (( BigDecimal.ONE).subtract(
                            reduction.divide(hundred, 4, RoundingMode.HALF_UP))
                    )
            );
        }

        //  result = ( (realCost * quantity * (goodTtax + 1)) + (processingFee + processingFee * (taxServicePercentage / 100.0)) );
        BigDecimal part1 = realCost
                .multiply(quantity)
                .multiply(goodTax.add(BigDecimal.ONE));
        // processingFee + processingFee * (taxServicePercentage / 100)
        BigDecimal part2 =
                processingFee
                        .add(
                                processingFee.multiply(
                                        taxServicePercentage.divide(BigDecimal.valueOf(100),4, RoundingMode.HALF_UP)
                                )
                        );
        // final result
        return part1.add(part2);
    }

    @Transient
    public BigDecimal calculateProcessingFeeServiceTax() {
        // double result = processingFee * getTaxServicePercentage() / 100.0;
        return processingFee.multiply(getTaxServicePercentage()).divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal calculateCostTaxPortion() {
        // double result = getTotal() - (processingFee + TaxPortionOfProcessingFeeService + (getQuantity() * sellExTaxPrice));
        BigDecimal part1 = quantity.multiply(sellExTaxPrice);
        BigDecimal part2 = processingFee.add(taxPortionOfProcessingFeeService).add(part1);
        return getTotal().subtract(part2);

    }
}