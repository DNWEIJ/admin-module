package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;


@Primary
@Component("correctFinancialServiceImpl")
// Overwrite the one in customer-module
public class FinancialService implements FinancialServiceInterface {
    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;

    public FinancialService(PaymentRepository paymentRepository, LineItemRepository lineItemRepository) {
        this.paymentRepository = paymentRepository;
        this.lineItemRepository = lineItemRepository;
    }

    public BigDecimal getCustomerBalance(Long customerId) {

        BigDecimal amountPaid = paymentRepository.getSumAmountOfPayment(customerId, AutorisationUtils.getCurrentUserMid());
        BigDecimal amountOutStanding = lineItemRepository.getSumAmountOfLineItem(customerId, AutorisationUtils.getCurrentUserMid());
        return amountPaid.subtract(amountOutStanding);
    }

    public LocalDate getLastestPaymentDate(Long customerId) {
        return paymentRepository.getLatestPaymentDate(customerId, AutorisationUtils.getCurrentUserMid());
    }
}