package dwe.holding.salesconsult.sales.model;

import dwe.holding.salesconsult.consult.model.PaymentVisit;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.shared.model.converter.PaymentMethodEnumConverter;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "SALES_PAYMENT")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment extends TenantBaseBO {
    @Column(nullable = false)
    private LocalDate paymentDate;
    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, columnDefinition = "varchar(1)")
    @Convert(converter = PaymentMethodEnumConverter.class)
    private PaymentMethodEnum method;

    private String referenceNumber;
    @Lob
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    @Builder.Default
    private Set<PaymentVisit> paymentVisits = new HashSet<>(0);
}