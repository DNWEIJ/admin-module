package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import dwe.holding.salesconsult.consult.repository.LookupRoomRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.IntStream;

import static dwe.holding.salesconsult.consult.controller.VisitController.VISIT_URL;
import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateReasonsInModel;
import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateRoomsInModel;

@RequestMapping("/generic")
@Controller
@AllArgsConstructor
public class AddOrDeletePetToAppointmentModalController {
    private final CustomerService customerService;
    private final AppointmentVisitService appointmentVisitService;
    private final AppointmentRepository appointmentRepository;
    private final VisitRepository visitRepository;
    private final LookupPurposeRepository lookupPurposeRepository;
    private final UserService userService;
    private final LookupRoomRepository lookupRoomRepository;
    public static final String OTC_URL = "/sales/otc/customer/{customerId}/visit/{visitId}";

    @GetMapping("/customer/{customerId}/appointment/{appointmentId}/addpet/{salesType}")
    String getModalAddPetHtmxModal(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable SalesType salesType, Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        if (customer == null) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");// todo set header for error
            // todo fix error handling
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        // validate appointment
        if (app.isCancelled() || app.isCompleted()) {
            // todofix error hadnling
            redirect.addFlashAttribute("message", "Appointment is cancelled or completed.");
            return "redirect:/sales/otc/search/";
        }

        List<Long> petOnVisit = app.getVisits().stream().map(visit -> visit.getPet().getId()).toList();
        updateRoomsInModel(model, lookupRoomRepository);
        updateReasonsInModel(model, lookupPurposeRepository);

        model
                .addAttribute("salesType", salesType)
                .addAttribute("customerId", customer.id())
                .addAttribute("appointmentId", app.getId())
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).filter(pet -> !petOnVisit.contains(pet.id())).toList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("timeList", IntStream.rangeClosed(1, 24).map(i -> i * 5).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList())
        ;
        return "sales-module/fragments/htmx/dialogaddpet";
    }

    @PostMapping("/customer/{customerId}/appointment/{appointmentId}/addpet/{salesType}")
    String saveAddPetXhtmlModal(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, Long selectPetId, @ModelAttribute OTCSelectController.PetsForm petsForm,
                                Model model, RedirectAttributes redirect, HttpServletResponse response) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<AppointmentVisitService.CreatePet> pets = petsForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();

        if (customer == null || pets.isEmpty()) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        appointmentVisitService.addPetsToAppointment(customerId, pets, app);
        model
                .addAttribute("selectedPetId", selectPetId)
                .addAttribute("petsOnAppointment", app.getVisits().stream().map(visit -> new PresentationElement(visit.getId(), visit.getPet().getNameWithDeceased())).toList());
        response.setHeader("HX-Trigger", "closeModal");
        return "sales-module/fragments/htmx/petselectpetdropdown";
    }

    @DeleteMapping("/customer/{customerId}/visit/{visitId}/pet/{petId}")
    String deletePetFromAppointment(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, @NotNull @PathVariable Long petId,
                                    HttpServletResponse response,    @RequestHeader(value = "HX-Current-URL", required = false) String hxCurrentUrl) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);

        Visit visit = appointmentVisitService.deletePetFromAppointment(
                visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow()
                , petId);
        // no modal call
        if(hxCurrentUrl == null || hxCurrentUrl.isEmpty()) {
            response.setHeader("HX-Trigger", "{\"refreshPage\": {\"url\":\""
                    + VISIT_URL.replace("{customerId}", customer.id().toString()).replace("{visitId}", visit.getId().toString())
                    + "?callFrom=customer\"}}" // customer / agenda are the options
            );
        } else  {
            response.setHeader("HX-Trigger", "{\"refreshPage\": {\"url\":\""
                    + OTC_URL.replace("{customerId}", customer.id().toString()).replace("{visitId}", visit.getId().toString())
                    + "?callFrom=customer\"}}" // customer / agenda are the options
            );

        }
        return "fragments/elements/empty";
    }
}