package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.supplyinventory.model.converter.PricingTypeEnumConverter;
import dwe.holding.supplyinventory.model.type.PricingTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    // OR
    // you give x for y
    // NOT at the same time!!!
    // we wills store zeros insteand of nulls, to have more clearity
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal salesPriceExTax;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal processingFee;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal reductionPercentage;

    // format is "5|4" <- buy 5 pay 4
    // We add the shortCode on the line item: (4/5)
    // if we find 10 elements,
    //   we create 1 lineitem: with 8 quantity and the symbol (4/5)
    //   we create 1 lineitem: witn quantity 2 with the normal price
    int buyXforY_X;
    int buyXforY_Y;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = PricingTypeEnumConverter.class)
    PricingTypeEnum pricingType;
}