package dwe.holding.reporting.service;

import dwe.holding.customer.expose.CustomerService;
import dwe.holding.reporting.repository.DocumentTemplateRepository;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Component
@AllArgsConstructor
public class ReportingService {
    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;
    private final CustomerService customerService;
    private final VisitRepository visitRepository;
    private final DocumentTemplateRepository documentTemplateRepository;

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