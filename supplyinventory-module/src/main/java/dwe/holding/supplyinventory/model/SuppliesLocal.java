package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLY_SUPPLIESLOCAL", uniqueConstraints = @UniqueConstraint(columnNames = {"SUPPLIES_ID", "MLID"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SuppliesLocal extends TenantBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Supplies supplies;
    private Double quantity;
    private Double individualQuantity;
    private Double minQuantity;
    private Double buyQuantity;
}