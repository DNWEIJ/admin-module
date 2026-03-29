package dwe.holding.customer.client.service;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class CustomerFinancialInfo {
    private final SessionStorageCustomer customerStorage;
    private final SessionStorageFinancials financialsStorage;
    private final FinancialServiceInterface financialService;

    public Long getCustomerId() {
        return customerStorage.getCustomer().getId();
    }

    public void updateCustomerAndFinancialInfo(Model model, Customer customer) {
        if (customer == null) {
            model
                    .addAttribute("customerInformation", null)
                    .addAttribute("balanceInfo", null)
                    .addAttribute("lastPayment", null)
            ;
            return;
        }

        customerStorage.setCustomer(new SessionStorageCustomer.CustomerSettings(customer.getId(), customer.getCustomerNameWithId()));
        financialsStorage.setFinancials(
                new SessionStorageFinancials.FinancialSettings(
                        customer.getBalance() == null? BigDecimal.ZERO : customer.getBalance(),
                        financialService.getLastestPaymentDate(customer.getId()),
                        financialService.getLastestPaymentAmount(customer.getId())
                )
        );
        setToModel(model);
    }

    public void setToModel(Model model) {
        model
                .addAttribute("customerInformation", customerStorage.getCustomer().getName())
                .addAttribute("balanceInfo", financialsStorage.getFianncials().getBalance())
                .addAttribute("lastPaymentDate", financialsStorage.getFianncials().getLastPaymentDate())
                .addAttribute("lastPaymentAmount", financialsStorage.getFianncials().getLastPaymentAmount())
        ;
    }

    public void customerReset(Model model) {
        updateCustomerAndFinancialInfo(model, null);
    }
}
