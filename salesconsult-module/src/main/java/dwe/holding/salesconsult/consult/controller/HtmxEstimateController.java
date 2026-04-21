package dwe.holding.salesconsult.consult.controller;

import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.mapper.LineItemMapper;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.service.EstimateService;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.ProductService;
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
    private final ProductService productService;

    @PostMapping("/customer/{customerId}/estimate/{estimateId}/pet/{petId}/lineitem")
    String saveHtmxEstimateLineItem(@PathVariable Long customerId, @PathVariable Long estimateId, @PathVariable Long petId,
                                    @NotNull BigDecimal inputProductQuantity, @NotNull Long inputProductId,
                                    Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomerFromPet(petId);
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/otc/search/";
        }
        Estimate estimate = estimateService.getEstimate(estimateId, petId);

        estimateService.saveEstimateLineItems(lineItemMapper.toEstimateLineItemList(
                        lineItemService.createEstimateLineItem(inputProductId, inputProductQuantity, customerService.getPet(customerId, petId))
                        , estimate
                )
        );

        Appointment app = Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build();
        Visit visit = Visit.builder().appointment(app).build();
        model
                .addAttribute("productSearchUrl", EstimateController.getUrl(customerId, petId, estimate.getId()))
                .addAttribute("appointment", app)
                .addAttribute("visit", visit)
                .addAttribute("categoryNames", productService.getAllCategoriesInclDeleted());
        ModelHelper.updateLineItemsInModel(model, estimateService.getAllLineItems(estimateId, petId));
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }
}