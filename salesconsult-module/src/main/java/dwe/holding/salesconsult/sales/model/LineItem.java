package dwe.holding.salesconsult.sales.model;


import dwe.holding.admin.exception.ApplicationException;
import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.supplyinventory.model.LookupCostingCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SALES_LINEITEM")
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
    private Long patientId;

    @NotNull
    private Long categoryId;

    @NotEmpty
    @Column(nullable = false)
    private String nomenclature;

    @NotNull
    @Column(nullable = false)
    private Double quantity;

    @NotNull
    private TaxedTypeEnum taxForSellExTaxPrice;

    private Double taxGoodPercentage;
    private Double taxServicePercentage;
    private Double sellExTaxPrice;
    private Double processingFee;
    private Double total;
    private Double TaxPortionOfSell;
    private Double TaxPortionOfProcessingFeeService;

    @Transient
    public Double calculateTotal(Double reduction) {
        double goodTtax = 0.0;

        if (TaxedTypeEnum.GOOD.equals(taxForSellExTaxPrice)) {
            goodTtax = getTaxGoodPercentage() / 100.0;
        }
        if (TaxedTypeEnum.SERVICE.equals(taxForSellExTaxPrice)) {
            goodTtax = getTaxServicePercentage() / 100.0;
        }

        Double realCost = sellExTaxPrice; //real price
        if (reduction != null) {
            realCost = realCost * (1 - reduction / 100);
        }

        double result = ( (realCost * quantity * (goodTtax + 1)) + (processingFee + processingFee * (taxServicePercentage / 100.0)) );
        result = Math.round(result * 100);
        result = result / 100;

        return result;
    }

    @Transient
    public Double calculateProcessingFeeServiceTax() {
        double result = processingFee * getTaxServicePercentage() / 100.0;
        result = Math.round(result * 100);
        result = result / 100;
        return result;
    }

    @Transient
    public Double calculateCostTaxPortion() {
        double result = getTotal() - (processingFee + TaxPortionOfProcessingFeeService + (getQuantity() * sellExTaxPrice));
        result = Math.round(result * 100);
        result = result / 100;
        return result;
    }
}