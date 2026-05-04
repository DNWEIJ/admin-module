package dwe.holding.salesconsult.consult.controller;

import dwe.holding.customer.expose.CustomerService;
import dwe.holding.customer.service.ChipNummerClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
@Slf4j
public class HtmxVisitPetInfoController {
    private final CustomerService customerService;
    private final ChipNummerClient client;

    @GetMapping("/visit/customer/{customerId}/petinfo")
    String showAllPetInfo(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customer", customer);
        return "consult-module/fragments/htmx/petinfo";
    }

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/chipnr")
    String showChipInfo(@PathVariable Long customerId, @PathVariable Long petId, Model model, Locale locale) throws Exception {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);

        model.addAttribute("genericDialogData",  client.search(
                customer.pets().stream().filter(p -> p.id().equals(petId)).findFirst().orElseThrow().chipTattooId(),
                locale)
        );
        return "fragments/elements/genericdialog";
    }

    @GetMapping("/visit/customer/{customerId}/registerchipnr")
    String registerChipInfo(@PathVariable Long customerId, Model model) {
        // validate customer
        customerService.searchCustomer(customerId);

        model.addAttribute("genericDialogData", """
                <iframe src="https://ndg.nl/frm/reg" style="width: 100%; height: 100vh; border: 0;" ></iframe>
                """);
        return "fragments/elements/genericdialog";
    }
}