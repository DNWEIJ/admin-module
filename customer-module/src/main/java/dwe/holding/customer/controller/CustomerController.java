package dwe.holding.customer.controller;


import dwe.holding.customer.model.Customer;
import dwe.holding.customer.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(path = "/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping("/customer")
    String newRecord(Model model) {
        model.addAttribute("form", new form(true, false));
        model.addAttribute("customer", new Customer());
        return "customer-module/customer/action";
    }

    @GetMapping("/customer/{id}")
    String editRecord(@PathVariable Long id, Model model) {
        if (id == 0) {
            return newRecord(model);
        }
        model.addAttribute("form", new form(true, false));
        model.addAttribute("customer", customerRepository.findById(id).get());
        return "customer-module/customer/action";
    }

    @GetMapping("/customer/list")
    String listRecord() {
        return "customer-module/customer/action";
    }

    @PostMapping("/customer")
    String saveCustomer(@Valid Customer customerForm) {
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
        return "redirec:/customer";
    }

    @PostMapping("/search/customer")
    /**
     * Search for a customer via htmx:
     *   SearchCriteria can start with an I/i to indicate a search on Id.
     *   Two boolean fields to increase the search scope: everywhere and street.
     */
    String searchCustomerHtmx(Model model, String searchCriteria, boolean everywhere, boolean street) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "<ul style=\"max-height: 180px; overflow: auto;\"></ul>");
            model.addAttribute("form", new form(everywhere, street));
            return "fragments/elements/flatData";
        }
        if (searchCriteria.toLowerCase().charAt(0) == 'i') {
            // find on ID
            return editRecord(Long.parseLong(searchCriteria.substring(1)), model);
        }
        model.addAttribute("flatData", getCustomers(searchCriteria, everywhere, street));
        return "fragments/elements/flatData";
    }

    private Object getCustomers(String searchCriteria, boolean everywhere, boolean street) {
        List<String> listCustomers = new ArrayList<>();

        String escapedSearch = Pattern.quote(searchCriteria);
        Pattern pattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);

        if (!everywhere) {
            listCustomers.addAll(customerRepository.findByLastNameStartsWithAndMemberId(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        } else {
            listCustomers.addAll(customerRepository.findByLastNameContainingAndMemberId(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        }
        if (street) {
            listCustomers.addAll(customerRepository.findByAddressLineContainingAndMemberId(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        }
        log.info("found customers:" + listCustomers.size());
        return "<ul style=\"max-height: 180px; overflow: auto;\">" +
                String.join("", listCustomers)
                + "</ul>";
    }

    String getOption(Customer customer, Pattern pattern) {

        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(customer.getLastName());
        while (matcher.find()) {
            String match = matcher.group();
            matcher.appendReplacement(result, "<strong>" + match + "</strong>");
        }
        matcher.appendTail(result);
        // <li class="ac_even">♦<strong>Weij</strong>, D. - Fahrenheitsingel 86 - 1097NV</li>
        return "<li  data-id=" + customer.getId() + ">"
                + result + ", " + customer.getFirstName() + " - " + (customer.getAddressLine() == null ? "" : customer.getAddressLine())
                + " - " + customer.getZipCode() + "</li>";
    }

    record form(boolean startLastName, boolean includeStreetName) {
    }
}