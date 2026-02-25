package dwe.holding.customer.client.service;

import dwe.holding.admin.sessionstorage.SessionStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@AllArgsConstructor
public class SessionStorageFinancials {
    public static final String FINANCIAL = "financial";
    private final SessionStorage storage;

    public FinancialSettings getFianncials() {
        return read();
    }

   public void setFinancials(FinancialSettings settings) {
        update(settings);
    }

    private  FinancialSettings read() {
        return storage.getModule(FINANCIAL, SessionStorageFinancials.FinancialSettings.class).orElse(
                new FinancialSettings(null, null, null));
    }

    private void update(FinancialSettings updatedFinancials) {
        storage.updateModule(FINANCIAL, updatedFinancials);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class FinancialSettings {
        BigDecimal balance;
        LocalDate lastPaymentDate;
        BigDecimal lastPaymentAmount;
    }
}
