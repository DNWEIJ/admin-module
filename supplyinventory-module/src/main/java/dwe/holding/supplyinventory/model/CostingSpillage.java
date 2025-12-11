package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "SUPPLY_COSTING_SPILLAGE")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CostingSpillage extends TenantBaseBO {

    @Column(nullable = false)
    private Long costingId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 38, scale = 4)
    private
    BigDecimal packageAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;
}