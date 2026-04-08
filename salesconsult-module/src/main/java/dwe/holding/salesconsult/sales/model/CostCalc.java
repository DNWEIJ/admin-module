package dwe.holding.salesconsult.sales.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.supplyinventory.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
    private Long productId;

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


    public static BigDecimal calculateProcessingFeeServiceTax(BigDecimal processingFeeExTax, BigDecimal taxServicePercentage) {
        return processingFeeExTax
                .multiply(taxServicePercentage)
                .divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateCostTaxPortion(BigDecimal totalIncTax, BigDecimal processingFeeExTax,
                                                     BigDecimal taxPortionOfProcessingFeeService, BigDecimal quantity, BigDecimal salesPriceExTax
    ) {
        BigDecimal part1 = quantity.multiply(salesPriceExTax);
        BigDecimal part2 = processingFeeExTax.add(taxPortionOfProcessingFeeService).add(part1);
        return totalIncTax.subtract(part2);
    }

    public BigDecimal calculateTotal(BigDecimal reductionPercentage) {
        return Product.calculateTotal(salesPriceExTax, processingFeeExTax, quantity, taxedTypeEnum, taxGoodPercentage, taxServicePercentage,
                reductionPercentage);
    }

    public BigDecimal calculateProcessingFeeServiceTax() {
        return calculateProcessingFeeServiceTax(processingFeeExTax, taxServicePercentage);
    }

    public BigDecimal calculateCostTaxPortion() {
        return calculateCostTaxPortion(totalIncTax, processingFeeExTax, taxPortionOfProcessingFeeService, quantity, salesPriceExTax);
    }

}


//
//
//
//    public BigDecimal calculateTotal(BigDecimal reductionPercentage) {
//        BigDecimal hundred = new BigDecimal("100.0");
//
//        // this is for the product part, service is always serviceTax
//        BigDecimal useTaxPercentage = new BigDecimal("0.0");
//
//        if (TaxedTypeEnum.GOOD.equals(taxedTypeEnum)) {
//            useTaxPercentage = getTaxGoodPercentage().divide(hundred, 4, RoundingMode.HALF_UP);
//        }
//        if (TaxedTypeEnum.SERVICE.equals(taxedTypeEnum)) {
//            useTaxPercentage = getTaxServicePercentage().divide(hundred, 4, RoundingMode.HALF_UP);
//        }
//
//        BigDecimal realCost = salesPriceExTax; //real price
//        if (reductionPercentage != null) {
//            //  realCost = realCost * (1 - reductionPercentage / 100);
//            realCost = realCost.multiply(
//                    ((BigDecimal.ONE).subtract(
//                            reductionPercentage.divide(hundred, 4, RoundingMode.HALF_UP))
//                    )
//            );
//        }
//
//        //  result = ( (realCost * quantity * (goodTtax + 1)) + (processingFeeExTax + processingFeeExTax * (taxServicePercentage / 100.0)) );
//        BigDecimal part1 = realCost
//                .multiply(quantity)
//                .multiply(useTaxPercentage.add(BigDecimal.ONE));
//        // processingFeeExTax + processingFeeExTax * (taxServicePercentage / 100)
//        BigDecimal part2 =
//                processingFeeExTax
//                        .add(
//                                processingFeeExTax.multiply(
//                                        taxServicePercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
//                                )
//                        );
//        // final result
//        return part1.add(part2);
//    }
//
//    public BigDecimal calculateProcessingFeeServiceTax() {
//        // double result = processingFeeExTax * getTaxServicePercentage() / 100.0;
//        return processingFeeExTax.multiply(getTaxServicePercentage()).divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
//    }
//
//    public BigDecimal calculateCostTaxPortion() {
//        // double result = getTotal() - (processingFeeExTax + TaxPortionOfProcessingFeeExTaxService + (getQuantity() * salesPriceExtax));
//        BigDecimal part1 = quantity.multiply(salesPriceExTax);
//        BigDecimal part2 = processingFeeExTax.add(taxPortionOfProcessingFeeService).add(part1);
//        return getTotalIncTax().subtract(part2);
//    }
