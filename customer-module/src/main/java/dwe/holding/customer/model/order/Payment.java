package dwe.holding.customer.model.order;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.customer.model.Customer;
import dwe.holding.generic.shared.model.type.PaymentMethodEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "CUSTOMER_PAYMENT")
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

    @Column(nullable = false)
    private PaymentMethodEnum methods;

    private String referenceNumber;
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    private Set<PaymentVisit> paymentvisits = new HashSet<>(0);
}