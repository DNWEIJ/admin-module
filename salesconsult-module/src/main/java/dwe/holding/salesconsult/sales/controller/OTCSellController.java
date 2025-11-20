package dwe.holding.salesconsult.sales.controller;

import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
public class OTCSellController {
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final AppointmentRepository appointmentRepository;


    @GetMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}")
    String setupProductSell(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, 77L).orElseThrow(); // todo autorisationUtils.getCurrentMemberId()
        HashSet<Long> petsOnVisit = app.getVisits().stream().map(Visit::getPet).map(Pet::getId).collect(Collectors.toCollection(HashSet::new));

        model
                .addAttribute("appointment", app)
                .addAttribute("customer", customer)
                .addAttribute("petsOnVisit", app.getVisits().stream().map(Visit::getPet)
                        .map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased()))
                        .sorted(Comparator.comparing(PresentationElement::getId)).toList())
                .addAttribute("selectedPet",
                        app.getVisits().stream().filter(visit -> visit.getPet().getId().equals(petId)).toList().getFirst().getPet()
                )
                .addAttribute("pets", customer.pets().stream().filter(pet -> !petsOnVisit.contains(pet.id()) && !pet.deceased()))
                .addAttribute("deceasedPets", customer.pets().stream().filter(pet -> !petsOnVisit.contains(pet.id()) && pet.deceased()));
        updateModel(model, petId, app);
        return "sales-module/otc/productpage";
    }

    @PostMapping("/otc/search/{customerId}/sell/{appointmentId}/{petId}")
    String foundProductAddLineItemViaHtmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId, @NotNull @PathVariable Long petId,
                                          @NotNull BigDecimal inputCostingAmount, String inputBatchNumber, Long inputCostingId, String spillageName,
                                          Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomerFromPet(petId);
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Appointment app = appointmentRepository.findByIdAndMemberId(appointmentId, 77L).orElseThrow(); // AutorisationUtuls
        List<LineItem> newLineItems = lineItemService.createOTC(app, petId, inputCostingId, inputCostingAmount, inputBatchNumber, spillageName);
        updateModel(model, petId, app);
        return "sales-module/fragments/htmx/lineitemsoverview";
    }

    void updateModel(Model model, Long petId, Appointment app) {
        List<LineItem> lineItems = getLineItems(petId, app);
        model.addAttribute("allLineItems", lineItems);
        if (!lineItems.isEmpty()) {
            model.addAttribute("totalAmount", lineItems.stream().map(LineItem::getTotal).reduce(BigDecimal::add).get());
        }
    }

    private static List<LineItem> getLineItems(Long petId, Appointment app) {
        return app.getLineItems().stream().filter(lineItem -> lineItem.getPetId().equals(petId))
                .sorted(Comparator.comparing(LineItem::getId))
                .toList();
    }
}