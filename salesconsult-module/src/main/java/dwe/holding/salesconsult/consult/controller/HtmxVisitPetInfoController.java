package dwe.holding.salesconsult.consult.controller;

import dwe.holding.customer.expose.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
@Slf4j
public class HtmxVisitPetInfoController {
    private final CustomerService customerService;

    @GetMapping("/visit/customer/{customerId}/petinfo")
    String showAllPetInfo(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customer", customer);
        return "consult-module/fragments/htmx/petinfo";
    }
}