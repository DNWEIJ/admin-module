package dwe.holding.generic.suppliesandinventory.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLIESLOCAL", uniqueConstraints = @UniqueConstraint(columnNames = {"SUPPLIES_ID", "MLID"}))
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