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

@Entity(name = "SUPPLY_COSTING_GROUP")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CostingGroup extends MemberBaseBO {
    @NotNull
    @Column(nullable = false)
    private Long parentCostingId;

    @NotNull
    @Column(nullable = false)
    private Long childCostingId;

    @NotNull
    @Column(nullable = false)
    private Double quantity;
}