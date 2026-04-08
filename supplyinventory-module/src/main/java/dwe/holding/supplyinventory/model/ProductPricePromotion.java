package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "SUPPLY_PRODUCT_PRICE_PROMOTION")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductPricePromotion extends MemberBaseBO {

    @NotNull
    @Column(nullable = false)
    private Long productId;

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
    BigDecimal salesPriceExTax;

    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal processingFee;

    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal reductionPercentage;

    // format is "5|4" <- buy 5 pay 4
    // We add the shortCode on the line item: (4/5)
    // if we find 10 elements,
    //   we create 1 lineitem: with 8 quantity and the symbol (4/5)
    //   we create 1 lineitem: witn quantity 2 with the normal price
    String buyXforY;

}