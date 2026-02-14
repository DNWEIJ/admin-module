package dwe.holding.customer.client.controller;


import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.customer.client.mapper.CustomerMapper;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.service.SessionStorageCustomer;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerForm customerForm;
    private final SessionStorageCustomer sessionStorage;

    @GetMapping("/customer")
    String newRecord(@RequestParam(required = false) boolean isNew, Model model) {
        if (isNew) customerReset();
        else {
            Long customerId = sessionStorage.getCustomer().getId();
            if (customerId != null)
                return "redirect:/customer/customer/" + customerId;
        }

        model.addAttribute("form", customerForm);
        model.addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build());
        setModel(model);
        return "customer-module/customer/action";
    }

    @GetMapping("/customer/{id}")
    String editRecord(@PathVariable Long id, Model model) {
        if (id == 0) {
            return newRecord(true, model);
        }
        setModel(model);
        model.addAttribute("form", customerForm);
        Customer customer = customerRepository.findById(id).get();
        model.addAttribute("customer", customer);
        model.addAttribute("customerId", customer.getId());

        sessionStorage.setCustomer(new SessionStorageCustomer.CustomerSettings(customer.getId(), customer.getCustomerNameWithId()));

        return "customer-module/customer/action";
    }

    @PostMapping("/customer")
    String saveCustomer(@Valid Customer customerForm, RedirectAttributes redirect) {
        if (customerForm.getStatus() == null || customerForm.getNewsletter() == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/customer";
        }

        if (customerForm.isNew()) {
            Customer savedCustomer = customerRepository.save(
                    Customer.builder()
                            // fill per item all fields
                            .firstName(customerForm.getFirstName()).surName(customerForm.getSurName()).lastName(customerForm.getLastName()).middleInitial(customerForm.getMiddleInitial()).title(customerForm.getTitle())

                            .email(customerForm.getEmail()).homePhone(customerForm.getHomePhone()).workPhone(customerForm.getWorkPhone()).mobilePhone(customerForm.getMobilePhone())

                            .status(customerForm.getStatus()).newsletter(customerForm.getNewsletter())

                            // TODO: Add conversion to the address1,2,3 to normal fields; in copy_sql_customer address2,3 split into
                            .extraAddressInfo(customerForm.getExtraAddressInfo()).address2(customerForm.getAddress2()).address3(customerForm.getAddress3())
                            .oldAddressInfo(customerForm.getOldAddressInfo())
                            .zipCode(customerForm.getZipCode()).street(customerForm.getStreet()).streetNumber(customerForm.getStreetNumber()).city(customerForm.getCity())

                            .emergencyContact(customerForm.getEmergencyContact()).emergencyContactPhone(customerForm.getEmergencyContactPhone())

                            .previousVeterinarian(customerForm.getPreviousVeterinarian()).previousVeterinarianPhone(customerForm.getPreviousVeterinarianPhone())
                            .ubn(customerForm.getUbn())
                            .comments(customerForm.getComments())
                            .build()
            );
            return "redirect:/customer/customer/" + savedCustomer.getId();
        } else {
            Customer customer = customerRepository.findById(customerForm.getId()).get();
            if (!customer.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
                return "redirect:/customer/customer";
            }
            customerMapper.updateCustomerFromForm(customerForm, customer);
            redirect.addFlashAttribute("message", "label.saved");
            return "redirect:/customer/customer/" + customerRepository.save(customer).getId();
        }
    }

    private void setModel(Model model) {
        model
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("statusList", CustomerStatusEnum.getWebList());
    }

    private void customerReset() {
        sessionStorage.setCustomerId(null);
        sessionStorage.setCustomerName(null);
    }
}