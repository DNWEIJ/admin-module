package dwe.holding.salesconsult.sales.controller.otc;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.controller.HtmxVisitLineItemsController;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
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
import java.util.stream.Collectors;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateLineItemsInModel;
import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateVisitStatusInModel;
import static dwe.holding.salesconsult.sales.controller.ValidationHelper.validateAppointmenIsOk;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCSellController {
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final CostingService costingService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentVisitService appointmentVisitService;
    private final VisitRepository visitRepository;
    private final HtmxVisitLineItemsController htmxVisitLineItemsController;


    @GetMapping("/otc/customer/{customerId}/visit/{visitId}")
    String setupProductSell_InitialCall(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);

        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return "redirect:/sales/otc/search/";
        Appointment app = visit.getAppointment();
        HashSet<Long> listPetsOnAppointment = app.getVisits().stream().map(Visit::getPet).map(Pet::getId).collect(Collectors.toCollection(HashSet::new));

        updateVisitStatusInModel(model, visit.getStatus());
        model
                .addAttribute("customer", customer)
                // using the visit Id so switching between the pets is going ok since we change the url
                .addAttribute("petsOnAppointment", app.getVisits().stream().map(vist -> new PresentationElement(vist.getId(), vist.getPet().getNameWithDeceased()))
                        .sorted(Comparator.comparing(PresentationElement::getId)).toList())
                .addAttribute("selectedPet", visit.getPet())
                .addAttribute("pets", customer.pets().stream().filter(pet -> !listPetsOnAppointment.contains(pet.id()) && !pet.deceased()))
                .addAttribute("deceasedPets", customer.pets().stream().filter(pet -> !listPetsOnAppointment.contains(pet.id()) && pet.deceased()))
        ;
        updateModel(model, visit, customer.id());
        return "salesconsult-generic-module/productpage";
    }

    @DeleteMapping("/otc/customer/{customerId}/visit/{visitId}")
    ResponseEntity<?> deleteVisitFromAppointment(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, @NotNull @PathVariable Long petId, RedirectAttributes redirect) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("HX-Refresh", "true");
        // validate
        CustomerService.Customer customer = customerService.searchCustomer(customerId);

        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return new ResponseEntity<>(responseHeaders, HttpStatus.FORBIDDEN);
        visit = appointmentVisitService.deletePetFromAppointment(visit, petId);
        if (visit == null) {
            return new ResponseEntity<>(responseHeaders, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/otc/customer/{customerId}/visit/{visitId}/lineitem/{lineItemId}")
    String deleteLineItem(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, @NotNull @PathVariable Long lineItemId, RedirectAttributes redirect, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return "redirect:/sales/otc/search/"; // todo: to far back, see if we can just refresh the page..so it shows no lineitems adding anymore

        CustomerService.Customer customer = customerService.searchCustomerFromPet(visit.getPet().getId());
        if (!customer.id().equals(customerId)) {
            // TODO: should redirect to otc
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }

        visit = appointmentVisitService.deleteLineItemFromVisit(visit, lineItemId);

        updateModel(model, visit, customerId);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }

    @PostMapping("/otc/customer/{customerId}/visit/{visitId}/lineitem")
    String foundProductAddLineItemViaHtmx(@PathVariable Long customerId, @PathVariable Long visitId,
                                          @NotNull BigDecimal inputCostingQuantity, @NotNull Long inputCostingId,
                                          String inputBatchNumber, String spillageName, Model model, RedirectAttributes redirect) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return "redirect:/sales/otc/search/";// todo: to far back, see if we can just refresh the page..so it shows no lineitems adding anymore

        CustomerService.Customer customer = customerService.searchCustomerFromPet(visit.getPet().getId());
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        lineItemService.createOtcLineItem(visit.getAppointment(), visit.getPet().getId(), inputCostingId, inputCostingQuantity, inputBatchNumber, spillageName);
        updateModel(model, visit, customerId);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }


    private void updateModel(Model model,  Visit visit,Long customerId) {

        updateLineItemsInModel(model, lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()));
        model
                .addAttribute("costingSearchUrl", "/sales/otc/customer/" + customerId + "/visit/" + visit.getId())
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("salesType", SalesType.OTC);

    }
}