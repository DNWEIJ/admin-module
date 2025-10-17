package dwe.holding.customer.controller;


import dwe.holding.customer.model.Customer;
import dwe.holding.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping("/customer")
    String newRecord() {
        return "customer-module/customer/action";
    }

    @GetMapping("/customer")
    String editRecord() {
        return "customer-module/customer/action";
    }

    @GetMapping("/customer")
    String listRecord() {
        return "customer-module/customer/action";
    }

    @PostMapping("/customer")
    String saveCustomer(Customer customerForm, Model model) {
        customerRepository.save(
        Customer.builder()
                .address1(customerForm.getAddress1()).address2(customerForm.getAddress2()).address3(customerForm.getAddress3())
                .addressLine(customerForm.getAddressLine()).zipCode(customerForm.getZipCode()).city(customerForm.getCity()).state(customerForm.getState())
                .email(customerForm.getEmail()).mobilePhone(customerForm.getMobilePhone())
                .firstName(customerForm.getFirstName()).surname(customerForm.getSurname()).lastName(customerForm.getLastName())
                .middleInitial(customerForm.getMiddleInitial()).title(customerForm.getTitle())
                .emergencyContact(customerForm.getEmergencyContact()).emergencyContactPhone(customerForm.getEmergencyContactPhone())
                .status(customerForm.getStatus())
                .previousVeterinarian(customerForm.getPreviousVeterinarian()).previousVeterinarianPhone(customerForm.getPreviousVeterinarianPhone())
                .workPhone(customerForm.getWorkPhone()).mobilePhone(customerForm.getMobilePhone()).comments(customerForm.getComments())
                .build()
        );

        customerRepository.save(customerForm);
        return "customer-module/customer/searchList";
    }

    @PostMapping("/customer/search/customer")
    String searchCustomer(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customer-module/customer/searchList";
    }
}