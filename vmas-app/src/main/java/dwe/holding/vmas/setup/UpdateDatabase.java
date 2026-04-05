package dwe.holding.vmas.setup;

import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class UpdateDatabase {
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;
    private final VisitRepository visitRepository;

    @Transactional
    public void processAllCustomerBalance() {
        log.info("Processing all customers starting at: {}", LocalDateTime.now());
        long customerRecs = customerRepository.countByBalanceIsNull();
        if (customerRecs != 0) {
            customerRepository.findAllIds()
                    .forEach(customerId ->
                            customerRepository.updateBalance(customerId, getCustomerBalance(customerId))
                    );
        }
        log.info("Processing all customers finish at: {}", LocalDateTime.now());
    }

    @Transactional
    public void processAllVisitsBalance() {
        log.info("Processing all visits starting at: {}", LocalDateTime.now());
        long recIds = visitRepository.countByTotalAmountIncTaxEquals(BigDecimal.ZERO);
        if (recIds != 0) {
            visitRepository.zeroAmountOnVisit();
            visitRepository.updateAllTotalAmounts();
        }
        log.info("Processing all visits finish at: {}", LocalDateTime.now());
    }


    BigDecimal getCustomerBalance(Long customerId) {
        BigDecimal amountPaid = paymentRepository.getSumAmountOfPayment(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal amountOutStanding = lineItemRepository.getSumAmountOfLineItem(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal result = amountPaid.subtract(amountOutStanding);
        return result.abs().compareTo(new BigDecimal("0.01")) < 0 ? BigDecimal.ZERO : result;
    }
}
