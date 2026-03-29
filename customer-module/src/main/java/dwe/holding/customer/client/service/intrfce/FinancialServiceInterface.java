package dwe.holding.customer.client.service.intrfce;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinancialServiceInterface {
    LocalDate getLastestPaymentDate(Long customerId);

    BigDecimal getLastestPaymentAmount(Long id);

    void updateCustomerBalanceAndVisitTotal(Long customerId, Long visitId);
}
