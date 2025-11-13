package dwe.holding.customer.client.controller;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.admin.security.AutorisationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class ValidateCustomer {
    private final CustomerRepository customerRepository;
    public ValidateCustomer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean isInvalid(Long customerId, RedirectAttributes redirect) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        if (
                // todo un comment
//                customer.getId().equals(
        //        ((CustomerInformationHolder.CustomerInfo) AutorisationUtils.getInfoObject().getInformation()).customerId())
  //              &&
                (customer.getMemberId().equals(77L))  // TODO autorisationUtils.
        ) {
            return false;
        } else {
            // error -> invalid
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            AutorisationUtils.setInfoObject(null);
            return true;
        }
    }
}