package dwe.holding.customer.client.controller;


import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.service.ZipCodeApi;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/customer")
@AllArgsConstructor
@Slf4j
public class HtmxCustomerController {
    private final CustomerRepository customerRepository;
    private final ZipCodeApi zipCodeApi;
    private final MessageSource messageSource;


    private final String foundCustomers = """
            <ul>%s</ul>
            """;

    /* using the lookup for address via external service, we add the data elements here */
    private final String mainpart = """
            <div class="text-align-center" id="message">%s:</div>
            <div class="text-align-center" id="addressData" data-street="%s" data-number="%s" data-zipcode="%s" data-city="%s">%s</div>
            <div class="text-align-center" id="options">%s</div>
            """;

    private static final Pattern EMPTY_CASE_INSENSITIVE = Pattern.compile("", Pattern.CASE_INSENSITIVE);

    @GetMapping("/search/customer/address")
    public String getAddressModalViaHtmx() {
        return "customer-module/customer/searchaddress";
    }

    @GetMapping("/search/customer/address/{zipCode}/{houseNumber}")
    @ResponseBody
    public String searchAddressHtmx(@PathVariable String houseNumber, @PathVariable String zipCode, Locale locale) {
        Optional<ZipCodeApi.Address> optionalAddress = zipCodeApi.getAddress(zipCode, houseNumber);
        if (optionalAddress.isEmpty()) {
            return mainpart.formatted(messageSource.getMessage("address.notfound", null, locale), "", "", "", "", "", "");
        }

        ZipCodeApi.Address address = optionalAddress.get();
        if (address.street() == null && address.houseNumber() != null) {
            return mainpart.formatted(
                    messageSource.getMessage("address.notfound", null, locale),
                    "", "", "", "", "",
                    address.houseNumber().isEmpty()
                            ? ""
                            : messageSource.getMessage("address.possible.numbers", null, locale) + ":<br/>" + wrap(List.of(address.houseNumber()), locale)
            );
        }
        return mainpart.formatted(
                messageSource.getMessage("address.found", null, locale), address.street(), address.houseNumber(), address.zipCode(), address.city(), address.toString(),
                wrap(IfCustomerRecordExists(zipCode, houseNumber), locale)
        );
    }

    @GetMapping("/search/customer/address/street/{streetName}/{houseNumber}/{city}")
    @ResponseBody
    public String searchAddressViaStreetAndCityHtmx(Model model, @PathVariable String houseNumber, @PathVariable String streetName, @PathVariable String city, Locale locale) {
        Optional<ZipCodeApi.Address> optionalAddress = zipCodeApi.getAddressViaStreetAndCity(streetName, houseNumber, city);
        if (optionalAddress.isEmpty()) {
            return mainpart.formatted(messageSource.getMessage("address.notfound", null, locale), "", "", "", "", "", "");
        }
        ZipCodeApi.Address address = optionalAddress.get();
        if (address.street() == null && address.houseNumber() != null) {
            return mainpart.formatted(
                    messageSource.getMessage("address.notfound", null, locale), "", "", "", "", ""
                    , messageSource.getMessage("address.possible.numbers", null, locale) + ":<br/>" + wrap(List.of(address.houseNumber()), locale)
            );
        }
        return mainpart.formatted(
                messageSource.getMessage("address.found", null, locale), address.street(), address.houseNumber(), address.zipCode(), address.city(),
                address.toString(), wrap(IfCustomerRecordExists(address.zipCode(), address.houseNumber()), locale)
        );
    }

    private List<String> IfCustomerRecordExists(String zipCode, String houseNumber) {
        Pattern pattern = Pattern.compile(Pattern.quote(zipCode), Pattern.CASE_INSENSITIVE);
        List<Customer> list = customerRepository.findByZipCodeAndMemberId(zipCode, AutorisationUtils.getCurrentUserMid());
        return list.stream().filter(customer -> ((customer.getStreetNumber()!= null) && customer.getStreetNumber().contains(houseNumber)))
                .map(f -> getOption(f, pattern, new Selectors(false,false,false, false))).toList();
    }

    @GetMapping("/search/customer")
    /**
     * Search for a customer via htmx:
     *   SearchCriteria can start with an I/i to indicate a search on Id.
     *   SearchCriteria can start with an P/p to indicate a search on zipCode.
     *   Two boolean fields to increase the search scope: startLastName and includeStreetName.
     */
    public String searchCustomerHtmx(Model model, String searchCriteria, boolean startLastName, boolean includeStreetName, boolean includeFirstTel, boolean includePet, Locale locale) {
        Selectors falseSelectors = new Selectors(false,false,false, false);

        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "");
            model.addAttribute("form", new CustomerForm(startLastName, includeStreetName, includeFirstTel, includePet));
        } else {
            Pattern pattern = Pattern.compile(Pattern.quote(searchCriteria), Pattern.CASE_INSENSITIVE);
            if (searchCriteria.toLowerCase().charAt(0) == 'i') {
                // find on ID
                try {
                    Optional<Customer> maybeCustomer = customerRepository.findById(Long.parseLong(searchCriteria.substring(1)));
                    if (maybeCustomer.isPresent()) {
                        model.addAttribute("flatData", wrap(List.of(getOption(maybeCustomer.get(), EMPTY_CASE_INSENSITIVE, falseSelectors)), locale));
                        return "fragments/elements/flatData";
                    }
                } catch(NumberFormatException e) {
                    // do nothing;
                }
            }
            if (searchCriteria.toLowerCase().charAt(0) == 'z') {
                // find on zipCode
                List<String> maybeCustomer;
                if (searchCriteria.contains(",")) {
                    String[] searchCriteriaParts = searchCriteria.split(",");
                    maybeCustomer = customerRepository.findByZipCodeAndStreetNumberStartingWithAndMemberId(
                                    searchCriteriaParts[0].substring(1).toUpperCase(),
                                    searchCriteriaParts[1],
                                    AutorisationUtils.getCurrentUserMid()
                            )
                            .stream().sorted(Comparator.comparing(Customer::getLastName)).map(f -> getOption(f, pattern, falseSelectors)
                            ).toList();
                } else {
                    maybeCustomer = customerRepository.findByZipCodeAndMemberId(searchCriteria.substring(1).toUpperCase(), AutorisationUtils.getCurrentUserMid())
                            .stream().sorted(Comparator.comparing(Customer::getLastName)).map(f -> getOption(f, pattern, falseSelectors)
                            ).toList();
                }
                if (!maybeCustomer.isEmpty()) {
                    model.addAttribute("flatData", wrap(maybeCustomer, locale));
                    return "fragments/elements/flatData";
                }
            }
            // search other ways
            model.addAttribute("flatData", wrap(getCustomers(searchCriteria, startLastName, includeStreetName, includeFirstTel, includePet), locale));
        }
        return "fragments/elements/flatData";
    }

    private List<String> getCustomers(String searchCriteria, boolean startLastName, boolean includeStreetName, boolean includeFirstTel, boolean includePet) {
        List<String> listCustomers = new ArrayList<>();
        Selectors selectors = new Selectors(startLastName, includeStreetName, includeFirstTel, includePet);

        Pattern pattern = Pattern.compile(Pattern.quote(searchCriteria), Pattern.CASE_INSENSITIVE);

        if (startLastName) {
            listCustomers.addAll(customerRepository.getCustomerStartLastName(searchCriteria, AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern, selectors)
                    ).toList()
            );
        } else {
            listCustomers.addAll(customerRepository.getCustomerSomewhereLastName(searchCriteria, AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern, selectors)
                    ).toList()
            );
        }
        if (includeStreetName) {
            listCustomers.addAll(customerRepository.findByAddress2ContainingAndMemberIdOrderByLastNameAscFirstNameAsc(searchCriteria, AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern, selectors)
                    ).toList()
            );
        }
        if (includeFirstTel) {
            listCustomers.addAll(customerRepository.findByTelAndMemberIdOrderByLastNameAscFirstNameAsc(searchCriteria, AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f, pattern, selectors)
                    ).toList()
            );
        }
        if (includePet) {
            listCustomers.addAll(customerRepository.findByPet(searchCriteria, AutorisationUtils.getCurrentUserMid())
                    .stream().map(
                            f -> getOption(f.customer(), pattern, f.pet(), selectors)
                    ).toList()
            );
        }
        return listCustomers;
    }

    private String wrap(List<String> listCustomers, Locale locale) {
        return (listCustomers.size() > 0) ?
                "<ul>" + String.join("", listCustomers) + "</ul>"
                :
                foundCustomers.formatted(messageSource.getMessage("customer.notfound", null, locale));
    }

    private String getOption(Customer customer, Pattern pattern, Selectors selectors) {
        return getOption(customer, pattern, null,selectors);
    }

    private String getOption(Customer customer, Pattern pattern, Pet pet, Selectors selectors) {

            // must look like:  <li class="ac_even">♦<strong>van der Weij</strong>, D. - Fahrenheitsingel 86 - 1097NV</li>
        return "<li  data-id=" + customer.getId() + ">"
                + (customer.getStatus().equals(CustomerStatusEnum.CLOSED) ? "&#9670;" : "")
                + getStringText(customer.getSurName()) + highlightMatches(pattern, customer.getLastName(), true) + ", "
                + customer.getFirstName()
                + " - " + getStringText(highlightMatches(pattern, customer.getAddress2(), selectors.includeStreetName))
                + " - " + customer.getZipCode()
                + petDetails(pet, pattern, selectors.includePet)
                + "</li>";
    }

    private String petDetails(Pet pet, Pattern pattern, boolean needMatching) {
        if (pet == null) {
            return "";
        } else {
            return " - "
                    + highlightMatches(pattern, pet.getNameWithDeceased(), needMatching)
                    + " - " + getStringText(highlightMatches(pattern, pet.getChipTattooId(), needMatching))
                    + " - " + getStringText(highlightMatches(pattern, pet.getPassportNumber(), needMatching))
                    ;
        }
    }

    private String getStringText(String stringText) {
        return (stringText == null || stringText.isBlank()) ? "" : stringText + " ";
    }

    private void setModel(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("statusList", CustomerStatusEnum.getWebList());
    }

    private String highlightMatches(Pattern pattern, String input, boolean needMatching) {
        if (!needMatching) return input;

        StringBuffer result = new StringBuffer();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(result, "<strong>" + matcher.group() + "</strong>");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    record Selectors(boolean startLastName, boolean includeStreetName, boolean includeFirstTel, boolean includePet){}
}