package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateCustomerAndPetsInModel;
import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateReasonsInModel;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCSelectController {
    private final CustomerService customerService;
    private final AppointmentVisitService appointmentVisitService;
    private final LookupPurposeRepository lookupPurposeRepository;

    @GetMapping("/otc/search")
    String first_SearchCustomer(Model model) {
        model
                .addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build())
                .addAttribute("textLabel", "label.title.otc")
                .addAttribute("url", "/sales/otc/search/");
        return "salesconsult-generic-module/customersearchpage";
    }


    @GetMapping("/otc/search/{customerId}")
    String second_CustomerFound(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (customerId == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/ot/search";
        }
        model.addAttribute("form", new CustomerController.CustomerForm(true, false, false, false));
        updateReasonsInModel(model, lookupPurposeRepository);
        updateCustomerAndPetsInModel(model, customerService.searchCustomer(customerId));
        return "sales-module/otc/petselectpage";
    }


    @PostMapping("/otc/search/{customerId}")
    String selectedPets(@PathVariable Long customerId, @ModelAttribute PetsForm petsForm, Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<AppointmentVisitService.CreatePet> pets = petsForm.formPet().stream().filter(pet -> pet.checked() != null).toList();
        if (customer == null || pets.isEmpty()) {
            model.addAttribute("message", "Something went wrong. Please try again");
            return "sales-module/otc/searchpage";
        }
        Appointment app = appointmentVisitService.createAppointmentVisit(pets, customerId, SalesType.OTC);
        // start selling for the first pet in the list...
        return "redirect:/sales/otc/search/" + customerId + "/sell/" + app.getId() + "/" + app.getVisits().iterator().next().getPet().getId();
    }

    public record PetsForm(List<AppointmentVisitService.CreatePet> formPet) {
    }
}