package dwe.holding.customer.client.service.intrfce;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinancialServiceInterface {
    BigDecimal getCustomerBalance(Long customerId);
    LocalDate getLastestPaymentDate(Long customerId);
}
