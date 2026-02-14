package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.EstimateForPet;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.salesconsult.consult.service.EstimateService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateCustomerAndPetsInModel;
import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateReasonsInModel;

@RequestMapping("/consult")
@AllArgsConstructor
@Controller
public class EstimateController {
    private final CustomerService customerService;
    private final LookupPurposeRepository lookupPurposeRepository;
    private final EstimateService estimateService;
    private final CostingService costingService;

    @GetMapping("/customer/{customerId}/estimates")
    String showEstimateListForCustomer(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model
                .addAttribute("pets", customer.pets().stream().collect(Collectors.toMap(p -> p.id(), p -> p.deceased() ? p.name() + " &dagger;" : p.name())))
                .addAttribute("activeMenu", "estimates")
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap())
                .addAttribute("estimates", estimateService.listEstimatesForCustomerPets(
                                customer.pets().stream().map(CustomerService.Pet::id).toList()
                        )
                );
        return "consult-module/estimate/list";
    }

    @GetMapping("/customer/{customerId}/estimate")
    String stepOneShowPetsAndLocation(@PathVariable Long customerId, Model model) {
        updateReasonsInModel(model, lookupPurposeRepository);
        updateCustomerAndPetsInModel(model, customerService.searchCustomer(customerId));
        model
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap())
                .addAttribute("salesType", SalesType.ESTIMATE);
        return "salesconsult-generic-module/petanddateselectpage";
    }

    @PostMapping("/customer/{customerId}/estimate")
    String selectedPetsShowEstimate(@PathVariable Long customerId, PetsForm petsForm, Model model) {
        List<AppointmentVisitService.CreatePet> pets = petsForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();

        Estimate estimate = estimateService.createEstimate(pets, customerId);
        // start selling for the first pet in the list...
        return "redirect:/consult/customer/" + customerId + "/estimate/" + estimate.getId() + "/" + estimate.getEstimateForPets().iterator().next().getPet().getId();
    }

    @GetMapping("/customer/{customerId}/estimate/{estimateId}/{petId}")
    String showEstimeInformation(Model model, @PathVariable Long customerId, @PathVariable Long estimateId, @PathVariable Long petId) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Estimate estimate = estimateService.getEstimate(estimateId, petId);

        // validate if customer has the pet
        EstimateForPet estimateForPet = estimate.getEstimateForPets().stream().filter(a -> a.getPet().getId().equals(
                customer.pets().stream().filter(p -> p.id().equals(petId)).findFirst().orElseThrow().id()
        )).findFirst().orElseThrow();

        model
                .addAttribute("estimate", estimate)
                .addAttribute("estimateForPet", estimateForPet)
                .addAttribute("customer", customer)
                .addAttribute("selectedPet", estimateForPet.getPet())
                .addAttribute("petList", estimate.getEstimateForPets().stream().collect(Collectors.toMap(a -> a.getPet().getId(), a -> a.getPet().getNameWithDeceased()))
                )
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("appointment", Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build())
                .addAttribute("url", "/consult/customer/" + customerId + "/estimate/" + estimate.getId() + "/" + petId)
                .addAttribute("salesType", SalesType.ESTIMATE);
        ModelHelper.updateLineItemsInModel(model, estimateService.saveEstimateLineItems(estimate.getEstimatelineitems()));
        return "consult-module/estimate/estimateforpet";
    }

    public record PetsForm(List<AppointmentVisitService.CreatePet> formPet) {
    }
}