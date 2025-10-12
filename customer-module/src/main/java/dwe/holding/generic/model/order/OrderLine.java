package dwe.holding.generic.model.order;

import dwe.holding.generic.admin.model.base.TenantBaseBO;

import dwe.holding.generic.model.lookup.LookupCostingCategory;
import dwe.holding.generic.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "CUSTOMER_ORDERLINE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderLine extends TenantBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
    private LookupCostingCategory category;

    @Column(nullable = false)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String nomenclature;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private TaxedTypeEnum tax;

    private Double taxGoodPercentage;
    private Double taxServicePercentage;
    private Double cost;
    private Double processingFee;
    private Double total;
    private Double costTaxPortion;
    private Double processingFeeServiceTaxPortion;
}