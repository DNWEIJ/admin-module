package dwe.holding.customer.client.model.order;

import dwe.holding.admin.model.base.TenantBaseBO;

import dwe.holding.shared.model.type.TaxedTypeEnum;
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