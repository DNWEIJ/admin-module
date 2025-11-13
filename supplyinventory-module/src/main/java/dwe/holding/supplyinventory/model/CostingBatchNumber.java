package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity(name = "SUPPLY_COSTING_BATCH_NUMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CostingBatchNumber extends TenantBaseBO {

    private Long costingId;

    @NotEmpty
    @Column(nullable = false)
    private String batchNumber;

    private LocalDate startDate;
    private LocalDate endDate;
}