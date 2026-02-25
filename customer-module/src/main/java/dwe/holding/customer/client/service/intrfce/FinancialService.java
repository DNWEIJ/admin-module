package dwe.holding.customer.client.service.intrfce;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class FinancialService implements FinancialServiceInterface {
    public BigDecimal getCustomerBalance(Long customerId) {
        return null;
    }

    public LocalDate getLastestPaymentDate(Long customerId) {
        return null;
    }

    @Override
    public BigDecimal getLastestPaymentAmount(Long id) {
        return null;
    }
}
