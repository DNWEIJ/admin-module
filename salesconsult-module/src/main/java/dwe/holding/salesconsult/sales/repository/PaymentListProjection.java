package dwe.holding.salesconsult.sales.repository;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class PaymentListProjection {
    private Long paymentId;
    private LocalDate paymentDate;
    private Long localMemberId;
    private String referenceNumber;
    private PaymentMethodEnum method;
    private BigDecimal amount;
    @Setter
    public List<VisitIdAppointmentId> visitIdAppointmentId;

    // customer
    Long customerId;
    String lastName;
    String firstName;
    String surName;
    String middleInitial;

    public BigDecimal balance;
    public BigDecimal firstMonthPayment;
    public BigDecimal secondMonthPayment;
    public BigDecimal thirdMonthPayment;
    public BigDecimal fourthMonthPayment;

    public record VisitIdAppointmentId(Long visitId, Long appointmentId, YesNoEnum otc) {
        public boolean isOTC() {
            return otc.getDatabaseField().equals("Y");
        }
    }

    public String getCustomerName() {
        return Customer.getCustomerName(lastName, surName, firstName, middleInitial);
    }
}