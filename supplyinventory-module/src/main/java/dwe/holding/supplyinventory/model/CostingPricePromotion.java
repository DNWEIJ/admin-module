package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "SUPPLY_COSTING_PRICE_PROMOTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CostingPricePromotion extends MemberBaseBO {

    @NotNull
    @Column(nullable = false)
    private Long costingId;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    // either you give a change in price/processing fee
    // OR
    // you give a reduction percentage
    // NOT BOTH!!!
    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal sellExTaxPrice;

    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal processingFee;

    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal reductionPercentage;
}