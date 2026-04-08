package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.mapper.LineItemMapper;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.salesconsult.sales.model.Refund;
import dwe.holding.salesconsult.sales.repository.RefundRepository;
import dwe.holding.salesconsult.sales.repository.dsl.RefundListDsl;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.ProductService;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/sales/price")
@SessionAttributes("lineItems")
public class RefundController {
    public static final String SALES_PRICE_SELL = "/sales/price/customer/%s/refund";
    private final RefundListDsl refundListDsl;
    private final RefundRepository refundRepository;
    private final LineItemService lineItemService;
    LookupProductCategoryRepository lookupProductCategoryRepository;
    private final CustomerService customerService;
    private final LineItemMapper lineItemMapper;

    private final Appointment appointment = Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build();
    private final Visit visit = Visit.builder().status(VisitStatusEnum.CONSULT).appointment(appointment).build();


    @ModelAttribute("lineItems")
    public List<LineItem> lineItems() {
        return new ArrayList<>();
    }

    @GetMapping("/customer/{customerId}/refunds")
    String startCustomerRefund(Model model, @PathVariable Long customerId) {
        model
                .addAttribute("refunds", refundRepository.findByCustomerId(customerId))
                .addAttribute("localMembersMap", AutorisationUtils.getLocalMemberMap());
        return "sales-module/refund/list";
    }

    @GetMapping("/customer/{customerId}/refund")
    String newCustomerRefund(Model model, @PathVariable Long customerId) {
        ModelHelper.updateLineItemsInModel(model, new ArrayList());
        Refund newRefund = new Refund(customerId, LocalDate.now(), BigDecimal.ZERO, "", List.of());
        newRefund.setLocalMemberId(AutorisationUtils.getCurrentUserMlid());

        model
                .addAttribute("refund", newRefund)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList());
        updateModel(customerId, model);
        return "sales-module/refund/action";
    }


    @PostMapping("/customer/{customerId}/refund/lineitem")
    String foundProductAddLineItemViaHtmx(@PathVariable Long customerId, Model model,
                                          @NotNull Long inputCostingId, @NotNull BigDecimal inputCostingQuantity,
                                          @ModelAttribute(value = "lineItems") ArrayList<LineItem> lineItems) {
        List<LineItem> list = lineItemService.createPricing(inputCostingId, inputCostingQuantity);
        AtomicLong counter = new AtomicLong(lineItems.size() + 1);
        list.forEach((lineItem -> lineItem.setId(counter.getAndIncrement())));
        lineItems.addAll(list);

        ModelHelper.updateLineItemsInModel(model, lineItems);
        updateModel(customerId, model);

        return "sales-module/fragments/htmx/lineitemsfulltable";
    }

    @DeleteMapping("/customer/{customerId}/refund/lineitem/{lineItemId}")
    String deleteProductFromLineItemsViaHtmx(@NotNull @PathVariable Long customerId, @PathVariable Long lineItemId, @ModelAttribute("lineItems") List<LineItem> lineItems, Model model) {
        List<LineItem> currentLst = lineItems.stream().filter(lineitem -> !lineitem.getId().equals(lineItemId)).collect(Collectors.toList());
        ModelHelper.updateLineItemsInModel(model, currentLst);
        updateModel(customerId, model);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }

    @PostMapping("/customer/{customerId}/refund")
    String saveRefund(Refund refund, @PathVariable Long customerId,
                      @ProjectedPayload @ModelAttribute("lineItems") List<LineItem> lineItems) {

        if (refund.getId() == null) {
            CustomerService.Customer customer = customerService.searchCustomer(customerId);

            Refund tobeSavedRefund = Refund.builder()
                    .refundDate(refund.getRefundDate())
                    .amount(refund.getAmount())
                    .localMemberId(refund.getLocalMemberId())
                    .customerId(customer.id())
                    .build();
            tobeSavedRefund.setRefundLineItems(lineItemMapper.toRefundLineItemList(lineItems, tobeSavedRefund));
            refundRepository.save(tobeSavedRefund);
        } else {
            Refund ref = refundRepository.findByIdAndCustomerId(refund.getId(), customerId).orElseThrow();
            ref.setRefundLineItems(null);
            refundRepository.save(ref);

            // mapper changes id,version to null - dropping the refundLineItems and adding them again
            ref.setRefundLineItems(lineItemMapper.toRefundLineItemList(lineItems, ref));
            ref.setAmount(refund.getAmount());
            ref.setLocalMemberId(refund.getLocalMemberId());
            ref.setRefundDate(refund.getRefundDate());
            refundRepository.save(ref);
        }
        return "redirect:/sales/price/customer/" + customerId + "/refunds";
    }


    @GetMapping("/customer/{customerId}/refund/{refundId}")
        // Since for new we need to use a sessionStorage, to ensure we add the refund direclty with all info,
        // we will move the lines to the session lineitem and on save we will remove all lineitems and add them again.
        // Since refunds are not happing that much, the easiest solution instead of finding out changed/new etc.
    String getRefund(Model model, @NotNull @PathVariable Long customerId, @NotNull @PathVariable Long refundId) {
        customerService.searchCustomer(customerId);
        Refund refund = refundRepository.findByIdAndCustomerId(refundId, customerId).orElseThrow();
        model.addAttribute("refund", refund);
        // ids are null, set them
        AtomicLong counter = new AtomicLong(1);
        List<LineItem> changeLineItems = lineItemMapper.fromRefundLineItemList(refund.getRefundLineItems(), appointment);
        changeLineItems.forEach(lineItem -> lineItem.setId(counter.getAndIncrement()));

        ModelHelper.updateLineItemsInModel(model, changeLineItems);
        updateModel(customerId, model);
        return "sales-module/refund/action";
    }

    @GetMapping("/refund")
    String startFinancialRefund(Model model, RefundListForm form) {
        if (form.from == null || form.getIncludeTill() == null) {
            form = new RefundListForm(LocalDate.now().minusDays(1), LocalDate.now(), AutorisationUtils.getCurrentUserMlid());
        }
        model
                .addAttribute("adminList", true)
                .addAttribute("refunds", refundListDsl.findRefunds(form.localMemberId, form.from, form.includeTill))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("localMembersMap", AutorisationUtils.getLocalMemberMap())
                .addAttribute("ynvaluesList", YesNoEnum.getWebListDoNotCare())
                .addAttribute("form", form);
        return "sales-module/refund/list";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class RefundListForm {
        private LocalDate from;
        private LocalDate includeTill;
        private Long localMemberId;
    }

    private void updateModel(Long customerId, Model model) {
        model
                .addAttribute("productSearchUrl", SALES_PRICE_SELL.formatted(customerId))
                .addAttribute("categoryNames", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("visit", visit)
                .addAttribute("appointment", appointment)
                .addAttribute("salesType", SalesType.PRICE_INFO);
    }
}
