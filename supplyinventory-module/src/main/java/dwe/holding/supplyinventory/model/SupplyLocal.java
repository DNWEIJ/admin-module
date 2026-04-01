package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLY_SUPPLYLOCAL", uniqueConstraints = @UniqueConstraint(columnNames = {"SUPPLY_ID", "MLID"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupplyLocal extends LocalAndMemberBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Supply supply;
    private Double quantity;
    private Double individualQuantity;
    private Double minQuantity;
    private Double buyQuantity;
}