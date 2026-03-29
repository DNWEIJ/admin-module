package dwe.holding.reporting.repository.projection;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class PaymentListProjection {
    private LocalDate paymentDate;
    private Long localMemberId;
    private String referenceNumber;
    private PaymentMethodEnum method;
    private BigDecimal amount;

    // customer
    Long customerId;
    String lastName;
    String firstName;
    String surName;
    String middleInitial;

    public BigDecimal balance;
    @Setter
    public BigDecimal balanceFromDate;

    public String getCustomerName() {
        return Customer.getCustomerName(lastName, firstName, surName, middleInitial);
    }
}