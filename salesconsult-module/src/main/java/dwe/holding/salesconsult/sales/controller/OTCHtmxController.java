package dwe.holding.salesconsult.sales.controller;

import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.InvoiceStatusEnum;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCHtmxController {
    private final CustomerService customerService;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/otc/search/{customerId}/sell/{appointmentId}/addpet")
    String getModalAddPetHtmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId,
                              Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        if (customer == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, 77L).orElseThrow(); // AutorisationUtuls

        // validate appointment
        if (app.isCancelled() || app.iscompleted()) {
            redirect.addFlashAttribute("message", "Appointment is cancelled or completed.");
            return "redirect:/sales/otc/search/";
        }

        // prepare model info
        List<Long> petOnVisit = app.getVisits().stream().map(visit -> visit.getPet().getId()).toList();
        model.addAttribute("customerId", customer.id())
                .addAttribute("appointmentId", app.getId())
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                .addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("reasons", customerService.getReasons().stream()
                        .map(rec -> new PresentationElement(rec.getId(), rec.getDefinedPurpose(), true)).toList()
                );
        return "sales-module/fragments/htmx/dialogaddpet";
    }

    @PostMapping("/otc/search/{customerId}/sell/{appointmentId}/addpet")
    String saveAddPetXhtml(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @ModelAttribute PetsForm petsForm, Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        if (customer == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, 77L).orElseThrow(); // AutorisationUtuls

        app.getVisits().addAll(
                petsForm.getFormPet().stream().filter(pet -> pet.getChecked() != null).map(formPet ->
                        Visit.builder()
                                .appointment(app)
                                .pet(customerService.getPet(customerId, formPet.getId()))
                                .room("")
                                .purpose(formPet.getPurpose() == null ? "" : formPet.getPurpose())
                                .estimatedTimeInMinutes(5)
                                .veterinarian("user") // AutorisationUtils.getCurrentUserAccount())
                                .status(VisitStatusEnum.WAITING)
                                .sentToInsurance(YesNoEnum.No)
                                .invoiceStatus(InvoiceStatusEnum.NEW)
                                .build()
                ).collect(Collectors.toSet()));
        Appointment savedApp = appointmentRepository.save(app);
        // todo if visit fo somewhere else, this is for OTC
        return "redirect:/sales/otc/search/" + customerId + "/sell/" + appointmentId + '/' + savedApp.getVisits().stream().mapToLong(visit -> visit.getPet().getId()).max().orElseThrow();
    }
}