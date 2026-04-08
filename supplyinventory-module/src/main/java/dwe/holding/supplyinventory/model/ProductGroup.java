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

@Table(name = "SUPPLY_PRODUCT_GROUP")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductGroup extends MemberBaseBO {
    @NotNull
    @Column(nullable = false)
    private Long parentProductId;

    @NotNull
    @Column(nullable = false)
    private Long childProductId;

    @NotNull
    @Column(nullable = false,precision = 38, scale = 4)
    private
    BigDecimal quantity;
}