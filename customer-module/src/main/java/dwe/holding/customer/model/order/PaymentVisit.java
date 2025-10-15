package dwe.holding.customer.model.order;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity(name = "CUSTOMER_PAYMENT_VISIT")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class PaymentVisit extends TenantBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private Payment payment;
// TODO
    //    @ManyToOne(fetch = FetchType.EAGER)
    //    @JoinColumn(name = "VISIT_ID", nullable = false)
    //   private Visit visit;
}