package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.supplyinventory.expose.CostingService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateLineItemsInModel;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCSellController {
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final CostingService costingService;
    private final AppointmentRepository appointmentRepository;


    @GetMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}")
    String setupProductSell_InitialCall(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId, Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        final Appointment app = getAndValidateAppointment(appointmentId, redirect);
        if (app == null) return "redirect:/sales/otc/search/";

        HashSet<Long> petsOnVisit = app.getVisits().stream().map(Visit::getPet).map(Pet::getId).collect(Collectors.toCollection(HashSet::new));
        // after refresh the petId isn't available anymore on the visit, so select the first one then....
        List<Visit> visits = app.getVisits().stream().filter(visit -> visit.getPet().getId().equals(petId)).toList();
        Pet selectedPet = (visits.isEmpty()) ? app.getVisits().iterator().next().getPet() : visits.getFirst().getPet();

        model
                .addAttribute("appointment", app)
                .addAttribute("customer", customer)
                .addAttribute("petsOnVisit", app.getVisits().stream().map(Visit::getPet)
                        .map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased()))
                        .sorted(Comparator.comparing(PresentationElement::getId)).toList())
                .addAttribute("selectedPet", selectedPet)
                .addAttribute("pets", customer.pets().stream().filter(pet -> !petsOnVisit.contains(pet.id()) && !pet.deceased()))
                .addAttribute("deceasedPets", customer.pets().stream().filter(pet -> !petsOnVisit.contains(pet.id()) && pet.deceased()))
                .addAttribute("url", "/sales/otc/search/" + customer.id() + "/sell/" + app.getId() + "/" + selectedPet.getId() + "/");
        updateModel(model, petId, app);
        return "sales-module/generic/productpage";
    }

    @DeleteMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}")
    ResponseEntity<?> deleteVisitFromAppointment(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId, RedirectAttributes redirect) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("HX-Refresh", "true");

        final Appointment app = getAndValidateAppointment(appointmentId, redirect);

        if (app == null || app.getVisits() == null || app.getVisits().isEmpty() || app.getVisits().size() == 1) {
            return new ResponseEntity<>(responseHeaders, HttpStatus.NOT_FOUND);
        }
        app.getVisits().removeIf(visit -> visit.getPet().getId().equals(petId));
        app.getLineItems().removeIf(lineItem -> lineItem.getPet().getId().equals(petId));
        appointmentRepository.save(app);
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}/{lineItemId}")
    String deleteLineItem(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId, @NotNull @PathVariable Long lineItemId, RedirectAttributes redirect, Model model) {
        // still allowed to delete line item?
        final Appointment app = getAndValidateAppointment(appointmentId, redirect);
        if (app == null) return "redirect:/sales/otc/search/";

        lineItemService.delete(lineItemId);

        Appointment newApp = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        model.addAttribute("url", "/sales/otc/search/" + customerId + "/sell/" + app.getId() + "/" + petId + "/");
        updateModel(model, petId, newApp);
        return "sales-module/fragments/htmx/lineitemsoverview";
    }

    // TODO do we need to add version ?
    @PostMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}")
    String foundProductAddLineItemViaHtmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId,
                                          @NotNull BigDecimal inputCostingAmount, String inputBatchNumber, Long inputCostingId, String spillageName,
                                          Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomerFromPet(petId);
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        lineItemService.createOtcLineItem(app, petId, inputCostingId, inputCostingAmount, inputBatchNumber, spillageName);
        updateModel(model, petId, app);
        model.addAttribute("url", "/sales/otc/search/" + customerId + "/sell/" + app.getId() + "/" + petId + "/");
        return "sales-module/fragments/htmx/lineitemsoverview";
    }


    private Appointment getAndValidateAppointment(Long appointmentId, RedirectAttributes redirect) {
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        if (app.isCancelled() || app.iscompleted()) {
            redirect.addFlashAttribute("message", "Appointment is cancelled or completed.");
            return null;
        }
        return app;
    }

    private void updateModel(Model model, Long petId, Appointment app) {

        updateLineItemsInModel(model, lineItemService.getLineItemsForPet(petId, app.getId()));
        model
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("salesType", SalesType.OTC);

    }
}