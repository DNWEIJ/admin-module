package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


@Primary
@Component("correctFinancialServiceImpl")
@AllArgsConstructor
// Overwrite the one in customer-module
public class FinancialService implements FinancialServiceInterface {
    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;
    private final CustomerService customerService;
    private final VisitRepository visitRepository;

    public LocalDate getLastestPaymentDate(Long customerId) {
        return paymentRepository.findMaxPaymentDate(AutorisationUtils.getCurrentUserMid(), customerId).stream()
                .filter(p -> p.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Payment::getPaymentDate).findFirst().orElse(null);
    }

    @Override
    public BigDecimal getLastestPaymentAmount(Long customerId) {
        return paymentRepository.findMaxPaymentDate(AutorisationUtils.getCurrentUserMid(), customerId).stream()
                .filter(p -> p.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Payment::getAmount)
                .findFirst()
                .map(amount -> amount.setScale(6, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }

    public void updateCustomerBalanceAndVisitTotal(Long customerId, Long visitId) {
        BigDecimal amountPaid = paymentRepository.getSumAmountOfPayment(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal amountOutStanding = lineItemRepository.getSumAmountOfLineItem(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal result = amountPaid.subtract(amountOutStanding);

        customerService.updateCustomerBalance(customerId,
                result.abs().compareTo(new BigDecimal("0.01")) < 0 ? BigDecimal.ZERO : result
        );
        // no need when a payment is added
        if (!visitId.equals(0L)) {
            Visit visit = visitRepository.findById(visitId).orElseThrow();
            visit.setTotalAmountIncTax(
                    lineItemRepository.getSumAmountOfLineItemOnVisit(visit.getPet().getId(), visit.getAppointment().getId()).setScale(6, RoundingMode.HALF_UP)
            );
            visit.setTotalServiceTax(
                    lineItemRepository.getSumAmountOfLineItemServiceOnVisit(visit.getPet().getId(), visit.getAppointment().getId()).setScale(6, RoundingMode.HALF_UP)
            );
            visit.setTotalProductTax(
                    lineItemRepository.getSumAmountOfLineItemProductOnVisit(visit.getPet().getId(), visit.getAppointment().getId()).setScale(6, RoundingMode.HALF_UP)
            );
            visitRepository.save(visit);
        }
    }
}