package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLY_PRODUCT_SPILLAGE_USAGE")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductSpillageUsage extends MemberBaseBO {

    @Column(nullable = false)
    private Long productSpillageId;

    @Column(nullable = false)
    private Long lineItemId;
}