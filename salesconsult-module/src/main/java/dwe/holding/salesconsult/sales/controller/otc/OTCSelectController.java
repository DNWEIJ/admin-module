package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.InvoiceStatusEnum;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.controller.PetsForm;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCSelectController {
    private final CustomerService customerService;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/otc/search")
    String first_SearchCustomer(Model model) {
        model.addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build());

        return "sales-module/otc/searchpage";
    }


    @GetMapping("/otc/search/{customerId}")
    String second_CustomerFound(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (customerId == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/ot/search";
        }

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customer", customer)
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).toList())
                .addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("reasons", customerService.getReasons().stream()
                        .map(rec -> new PresentationElement(rec.getId(), rec.getDefinedPurpose(), true)).toList()
                );
        return "sales-module/otc/petselectpage";
    }


    @PostMapping("/otc/search/{customerId}")
    String selectedPets(@PathVariable Long customerId, @ModelAttribute PetsForm petsForm, Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<PetsForm.FormPet> pets = petsForm.getFormPet().stream().filter(pet -> pet.getChecked() != null).toList();
        if (customer == null || pets.isEmpty()) {
            model.addAttribute("message", "Something went wrong. Please try again");
            return "sales-module/otc/searchpage";
        }
        Appointment app = saveOTC(pets, customerId);
        // start selling for the first pet in the list...
        return "redirect:/sales/otc/search/" + customerId + "/sell/" + app.getId() + "/" + app.getVisits().iterator().next().getPet().getId();
    }

    private Appointment saveOTC(List<PetsForm.FormPet> pets, Long customerId) {
        Appointment appointment = Appointment.builder()
                .OTC(YesNoEnum.Yes)
                .cancelled(YesNoEnum.No)
                .pickedUp(YesNoEnum.No)
                .completed(YesNoEnum.No)
                .visitDateTime(LocalDateTime.now())
                .localMemberId(AutorisationUtils.getCurrentUserMlid())
                .build();
        appointment.setVisits(
                pets.stream().map(formPet ->
                        Visit.builder()
                                .appointment(appointment)
                                .pet(customerService.getPet(customerId, formPet.getId()))
                                .room("")
                                .purpose(formPet.getPurpose() == null ? "" : formPet.getPurpose())
                                .estimatedTimeInMinutes(5)
                                .veterinarian(AutorisationUtils.getCurrentUserAccount())
                                .status(VisitStatusEnum.WAITING)
                                .sentToInsurance(YesNoEnum.No)
                                .invoiceStatus(InvoiceStatusEnum.NEW)
                                .build()
                ).collect(Collectors.toSet())
        );
        return appointmentRepository.save(appointment);
    }
}