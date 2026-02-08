package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.supplyinventory.expose.CostingService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateLineItemsInModel;
import static dwe.holding.salesconsult.sales.controller.ValidationHelper.validateAppointmenIsOk;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitLineItemsController {
    private final VisitRepository visitRepository;
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final CostingService costingService;

    @DeleteMapping("/visit/customer/{customerId}/visit/{visitId}/lineitem/{lineItemId}")
    String deleteLineItemViaHTmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, @PathVariable Long lineItemId, Model model, RedirectAttributes redirect) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return "redirect:/consult/visit/search"; // todo: to far back, see if we can just refresh the page..so it shows no lineitems adding anymore

        CustomerService.Customer customer = customerService.searchCustomerFromPet(visit.getPet().getId());
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/visit/search";
        }
        lineItemService.delete(lineItemId);

        updateModel(model, visit, customerId);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }


    @PostMapping("/visit/customer/{customerId}/visit/{visitId}/lineitem")
    String postLineItemViaHtmx(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId,
                               @NotNull BigDecimal inputCostingQuantity, String inputBatchNumber, Long inputCostingId, String spillageName, Model model, RedirectAttributes redirect) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (!validateAppointmenIsOk(visit.getAppointment(), redirect))
            return "redirect:/consult/visit/search"; // todo: to far back, see if we can just refresh the page..so it shows no lineitems adding anymore

        CustomerService.Customer customer = customerService.searchCustomerFromPet(visit.getPet().getId());
        if (!customer.id().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/sales/visit/search";
        }
        lineItemService.createOtcLineItem(visit.getAppointment(), visit.getPet().getId(), inputCostingId, inputCostingQuantity, inputBatchNumber, spillageName);
        updateModel(model, visit, customerId);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }



    private void updateModel(Model model, Visit visit, Long customerId) {
        updateLineItemsInModel(model, lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()));
        model
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("visit", visit)
                .addAttribute("salesType", SalesType.VISIT)
                .addAttribute("url", "/visit/customer/" + customerId + "/visit/" + visit.getId() + "/lineitem/");
    }
}