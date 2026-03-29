package dwe.holding.salesconsult.sales.repository.projection;

import dwe.holding.customer.client.model.Customer;import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class RefundProjection {
    Long id;
    Long customerId;
    String lastName;
    String firstName;
    String surName;
    String middleInitial;
    LocalDate refundDate;
    BigDecimal amount;
    String comments;
    Long localMemberId;

    public String getCustomerName() {
        return Customer.getCustomerName(lastName, surName, firstName, middleInitial);
    }
}
