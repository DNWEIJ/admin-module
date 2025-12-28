package dwe.holding.salesconsult.consult.controller;

import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.mapper.LineItemMapper;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.EstimateForPet;
import dwe.holding.salesconsult.consult.service.EstimateService;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
public class HtmxEstimateController {

    private final LineItemService lineItemService;
    private final LineItemMapper lineItemMapper;
    private final CustomerService customerService;
    private final EstimateService estimateService;
    private final CostingService costingService;

    @PostMapping("/customer/{customerId}/estimate/{estimateId}/{petId}")
    String saveHtmxEstimateLineItem(@PathVariable Long customerId, @PathVariable Long estimateId, @PathVariable Long petId,
                                    @NotNull BigDecimal inputCostingAmount, @NotNull Long inputCostingId, Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomerFromPet(petId);
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Estimate estimate = estimateService.getEstimate(estimateId, petId);

        estimateService.saveEstimateLineItems(lineItemMapper.toEstimateLineItemList(
                        lineItemService.createEstimateLineItem(inputCostingId, inputCostingAmount, customerService.getPet(customerId, petId))
                        , estimate
                )
        );

        // save estimate line items
        model
                .addAttribute("url", "/consult/customer/" + customerId + "/estimate/" + estimate.getId() + "/" + petId)
                .addAttribute("appointment", Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build())
                .addAttribute("categoryNames", costingService.getCategories());
        ModelHelper.updateLineItemsInModel(model, estimateService.getAllLineItems(estimateId, petId));
        return "sales-module/fragments/htmx/lineitemsoverview";
    }

    @PostMapping("/customer/{customerId}/estimateforpet")
    String saveHtmxEstimateForPet(@PathVariable Long customerId, EstimateForPet estimateForPetForm, Model model, RedirectAttributes redirect) {
        // validate the customer exists
        customerService.searchCustomer(customerId);
        model.addAttribute("estimateForPet",
                estimateService.saveEstimateForPet(estimateForPetForm.getId(), estimateForPetForm.getPurpose(), estimateForPetForm.getComments())
        );
        model.addAttribute("message", "Estimate saved successfully");
        return "consult-module/estimate/estimateforpetform";
    }
}