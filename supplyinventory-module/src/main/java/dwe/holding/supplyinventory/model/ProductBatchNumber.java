package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Table(name = "SUPPLY_PRODUCT_BATCH_NUMBER")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductBatchNumber extends LocalAndMemberBaseBO {

    private Long productId;

    @NotEmpty
    @Column(nullable = false)
    private String batchNumber;

    private LocalDate startDate;
    private LocalDate endDate;
}