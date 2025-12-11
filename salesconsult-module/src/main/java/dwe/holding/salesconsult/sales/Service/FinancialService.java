package dwe.holding.salesconsult.sales.Service;

import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class FinancialService {

    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;

    public BigDecimal getCustomerBalance(Long customerId, LocalDateTime limitDateTime) {

        BigDecimal amountPaid, amountReceived;
//        if (limitDateTime == null) {
//            amountPaid = paymentRepository.getSumAmountOfPayment(customerId, AutorisationUtils.getCurrentUserMid());
//            amountReceived = lineItemRepository.getSumAmountOfLineItem(customerId, AutorisationUtils.getCurrentUserMid());
//        } else {
//            amountPaid = paymentRepository.getSumAmountOfPayment(customerId, limitDateTime, AutorisationUtils.getCurrentUserMid());
//            amountReceived = lineItemRepository.getSumAmountOfLineItem(customerId, limitDateTime, AutorisationUtils.getCurrentUserMid());
//        }
//
//        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
//        if (amountReceived == null) amountReceived = BigDecimal.ZERO;

//        return amountPaid.subtract(amountReceived);
        return BigDecimal.ZERO;
    }

    public LocalDate getLastestPaymentDate(Long customerId){
        return null;
   //     return paymentRepository.getLatestPaymentDate(customerId, AutorisationUtils.getCurrentUserMid());
    }
}