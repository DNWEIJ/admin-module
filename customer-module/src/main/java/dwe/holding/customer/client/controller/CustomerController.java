package dwe.holding.customer.client.controller;


import dwe.holding.customer.CustomerInformationHolder;
import dwe.holding.customer.client.mapper.CustomerMapper;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(path = "/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @GetMapping("/customer")
    String newRecord(Model model) {
        model.addAttribute("form", new CustomerForm(true, false));
        model.addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build());
        // TODO: Activate!!
        AutorisationUtils.setInfoObject(null);
        setModel(model);
        return "customer-module/customer/action";
    }

    @GetMapping("/customer/{id}")
    String editRecord(@PathVariable Long id, Model model) {
        if (id == 0) {
            return newRecord(model);
        }
        setModel(model);
        model.addAttribute("form", new CustomerForm(true, false));
        Customer customer = customerRepository.findById(id).get();
        model.addAttribute("customer", customer);
        model.addAttribute("customerId", customer.getId());

        AutorisationUtils.setInfoObject(new CustomerInformationHolder(new CustomerInformationHolder.CustomerInfo(customer.getCustomerNameWithId(), customer.getId())));
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

                            .address1(customerForm.getAddress1()).address2(customerForm.getAddress2()).address3(customerForm.getAddress3())
                            .addressLine(customerForm.getAddressLine()).zipCode(customerForm.getZipCode()).city(customerForm.getCity()).state(customerForm.getState())

                            .emergencyContact(customerForm.getEmergencyContact()).emergencyContactPhone(customerForm.getEmergencyContactPhone())

                            .previousVeterinarian(customerForm.getPreviousVeterinarian()).previousVeterinarianPhone(customerForm.getPreviousVeterinarianPhone())
                            .ubn(customerForm.getUbn())
                            .comments(customerForm.getComments())
                            .build()
            );
            return "redirect:/customer/customer/" + savedCustomer.getId();
        } else {
            Customer customer = customerRepository.findById(customerForm.getId()).get();
            if (!customer.getMemberId().equals(77L)) { // TODO autorisationUtils.
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
                return "redirect:/customer/customer";
            }
            customerMapper.updateCustomerFromForm(customerForm, customer);
            redirect.addFlashAttribute("message", "label.customer.saved");
            return "redirect:/customer/customer/" + customerRepository.save(customer).getId();
        }
    }

    @PostMapping("/search/customer")
    /**
     * Search for a customer via htmx:
     *   SearchCriteria can start with an I/i to indicate a search on Id.
     *   Two boolean fields to increase the search scope: startLastName and includeStreetName.
     */
    public String searchCustomerHtmx(Model model, String searchCriteria, boolean startLastName, boolean includeStreetName) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "");
            model.addAttribute("form", new CustomerForm(startLastName, includeStreetName));
        } else {
            if (searchCriteria.toLowerCase().charAt(0) == 'i') {
                // find on ID
                Optional<Customer> maybeCustomer = customerRepository.findById(Long.parseLong(searchCriteria.substring(1)));
                if (maybeCustomer.isPresent()) {
                    model.addAttribute("flatData", wrap(List.of(getOption(maybeCustomer.get(), Pattern.compile(Pattern.quote(""), Pattern.CASE_INSENSITIVE)))));
                    return "fragments/elements/flatData";
                } else {
                    model.addAttribute("flatData", wrap(List.of()));
                }
            }

            model.addAttribute("flatData", wrap(getCustomers(searchCriteria, startLastName, includeStreetName)));
        }
        return "fragments/elements/flatData";
    }

    private List<String> getCustomers(String searchCriteria, boolean startLastName, boolean includeStreetName) {
        List<String> listCustomers = new ArrayList<>();

        String escapedSearch = Pattern.quote(searchCriteria);
        Pattern pattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);

        if (startLastName) {
            listCustomers.addAll(customerRepository.getCustomerStartLastName(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        } else {
            listCustomers.addAll(customerRepository.getCustomerSomewhereLastName(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        }
        if (includeStreetName) {
            listCustomers.addAll(customerRepository.findByAddressLineContainingAndMemberIdOrderByLastNameAscFirstNameAsc(searchCriteria, 77L) // AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern)
                    ).toList()
            );
        }
        log.info("found customers:" + listCustomers.size());
        return listCustomers;
    }

    private String wrap(List<String> listCustomers) {
        return (listCustomers.size() > 0) ?
                "<ul style=\"max-height: 180px; overflow: auto;\">" + String.join("", listCustomers) + "</ul>"
                : "<ul style=\"max-height: 180px; overflow: auto;\">No record found</ul>";
    }

    private String getOption(Customer customer, Pattern pattern) {

        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(customer.getLastName());
        while (matcher.find()) {
            String match = matcher.group();
            matcher.appendReplacement(result, "<strong>" + match + "</strong>");
        }
        matcher.appendTail(result);
        // must look like:  <li class="ac_even">♦<strong>van der Weij</strong>, D. - Fahrenheitsingel 86 - 1097NV</li>
        return "<li  data-id=" + customer.getId() + ">"
                + (customer.getStatus().equals(CustomerStatusEnum.CLOSED) ? "&#9670;" : "")
                + getStringText(customer.getSurName()) + result + ", "
                + customer.getFirstName()
                + " - " + getStringText(customer.getAddress2())
                + " - " + customer.getZipCode() + "</li>";
    }


    private String getStringText(String stringText) {
        return (stringText == null || stringText.isBlank()) ? "" : stringText + " ";
    }

    private void setModel(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("statusList", CustomerStatusEnum.getWebList());
    }

    public record CustomerForm(boolean startLastName, boolean includeStreetName) {
    }
}