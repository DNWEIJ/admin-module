package dwe.holding.supplyinventory.model;
import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity(name = "SUPPLY_COSTING_BATCH_NUMBER_LINEITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CostingBatchNumberLineItem extends MemberBaseBO  {

    @Column(nullable = false)
    private Long costingBatchNrId;
    @Column(nullable = false)
    private Long lineItemId;
}