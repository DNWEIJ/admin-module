package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateReasonsInModel;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCHtmxController {
    private final CustomerService customerService;
    private final AppointmentVisitService appointmentVisitService;
    private final AppointmentRepository appointmentRepository;
    private final LookupPurposeRepository lookupPurposeRepository;
    private final CustomerForm customerForm;

    @GetMapping("/otc/search/{customerId}/sell/{appointmentId}/addpet")
    String getModalAddPetHtmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId,
                              Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        if (customer == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        // validate appointment
        if (app.isCancelled() || app.iscompleted()) {
            redirect.addFlashAttribute("message", "Appointment is cancelled or completed.");
            return "redirect:/sales/otc/search/";
        }

        List<Long> petOnVisit = app.getVisits().stream().map(visit -> visit.getPet().getId()).toList();
        model.addAttribute("customerId", customer.id())
                .addAttribute("appointmentId", app.getId())
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                // todo do we need this?
                .addAttribute("form", customerForm);
        updateReasonsInModel(model, lookupPurposeRepository);
        return "sales-module/fragments/htmx/dialogaddpet";
    }

    @PostMapping("/otc/search/{customerId}/sell/{appointmentId}/addpet")
    String saveAddPetXhtml(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @ModelAttribute OTCSelectController.PetsForm petsForm, Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<AppointmentVisitService.CreatePet> pets = petsForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();

        if (customer == null || pets.isEmpty()) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        final Appointment savedApp = appointmentVisitService.addPetsToAppointment(customerId, pets, app);
        // todo if visit go somewhere else, this is for OTC
        return "redirect:/sales/otc/search/" + customerId + "/sell/" + appointmentId + '/' + savedApp.getVisits().stream().mapToLong(visit -> visit.getPet().getId()).max().orElseThrow();
    }
}