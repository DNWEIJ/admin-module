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

@Table(name = "SUPPLY_COSTING_BATCH_NUMBER_LINEITEM")
@Entity
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