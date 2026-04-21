package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.EstimateForPet;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.salesconsult.consult.service.EstimateService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.controller.ProductController;
import dwe.holding.supplyinventory.expose.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ProductService productService;

    @GetMapping("/customer/{customerId}/estimates")
    String showEstimateListForCustomer(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model
                .addAttribute("pets", customer.pets().stream().collect(Collectors.toMap(CustomerService.Pet::id, p -> p.deceased() ? p.name() + " &dagger;" : p.name())))
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
        return "consult-module/estimate/estimateselect";
    }

    @PostMapping("/customer/{customerId}/estimate")
    String selectedPetsShowEstimate(@PathVariable Long customerId, PetsForm petsForm) {
        List<AppointmentVisitService.CreatePet> pets = petsForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();

        Estimate estimate = estimateService.createEstimate(pets, customerId);
        // start selling for the first pet in the list...
        return "redirect:/consult/customer/" + customerId + "/estimate/" + estimate.getId() + "/pet/" + estimate.getEstimateForPets().iterator().next().getPet().getId();
    }

    @GetMapping("/customer/{customerId}/estimate/{estimateId}/pet/{petId}")
    String showEstimeInformation(Model model, @PathVariable Long customerId, @PathVariable Long estimateId, @PathVariable Long petId) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Estimate estimate = estimateService.getEstimate(estimateId, petId);

        // validate if customer has the pet
        EstimateForPet estimateForPet = estimate.getEstimateForPets().stream().filter(a -> a.getPet().getId().equals(
                customer.pets().stream().filter(p -> p.id().equals(petId)).findFirst().orElseThrow().id()
        )).findFirst().orElseThrow();

        Appointment app = Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build();
        Visit visit = Visit.builder().appointment(app).build();
        model
                .addAttribute("activeMenu", "estimates")
                .addAttribute("estimate", estimate)
                .addAttribute("estimateForPet", estimateForPet)
                .addAttribute("customer", customer)
                .addAttribute("selectedPet", estimateForPet.getPet())
                .addAttribute("petList", estimate.getEstimateForPets().stream().collect(Collectors.toMap(a -> a.getPet().getId(), a -> a.getPet().getNameWithDeceased()))
                )
                .addAttribute("categoryNames", productService.getCategoriesWithoutDeletedRecs())
                .addAttribute("appointment", app)
                .addAttribute("visit", visit)
                .addAttribute("productSearchUrl", getUrl(customerId, petId, estimate.getId()))
                .addAttribute("productSearchForm", new ProductController.ListForm(null, null, Boolean.FALSE))
                .addAttribute("salesType", SalesType.ESTIMATE);
        ModelHelper.updateLineItemsInModel(model, estimateService.saveEstimateLineItems(estimate.getEstimateLineItems()));
        return "consult-module/estimate/estimateforpet";
    }

    @GetMapping("/customer/{customerId}/estimate/{estimateId}/pet/{petId}/change")
    String changeToConsult() {
        // ask for:
        //         location / date/ time period <- maybe reask for doel / timeperiod.
        return "";
    }

    @PostMapping("/customer/{customerId}/estimateforpet")
    String saveEstimateForPet(@PathVariable Long customerId, EstimateForPet estimateForPetForm, Model model, RedirectAttributes redirect) {
        customerService.searchCustomer(customerId);
        model.addAttribute("estimateForPet",
                estimateService.saveEstimateForPet(estimateForPetForm.getId(), estimateForPetForm.getPurpose(), estimateForPetForm.getComments(), estimateForPetForm.getEstimate().getEstimateDate())
        );

        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/consult/customer/" + customerId + "/estimates";
    }

    public static String getUrl(Long customerId, Long petId, Long estimateId) {
        return "/consult/customer/" + customerId + "/estimate/" + estimateId + "/pet/" + petId;
    }

    public record PetsForm(List<AppointmentVisitService.CreatePet> formPet) {
    }
}