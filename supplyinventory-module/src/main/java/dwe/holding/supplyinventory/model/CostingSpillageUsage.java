package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity(name="SUPPLY_COSTING_SPILLAGE_USAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CostingSpillageUsage extends MemberBaseBO {

    @Column(nullable = false)
    private Long costingSpillageId;

    @Column(nullable = false)
    private Long lineItemId;
}