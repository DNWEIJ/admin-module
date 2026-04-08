package dwe.holding.salesconsult.sales.controller.price;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.controller.ProductController;
import dwe.holding.supplyinventory.expose.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
@SessionAttributes("lineItems")
public class PriceSellController {
    public static final String SALES_PRICE_SELL = "/sales/price/sell";
    private final LineItemService lineItemService;
    private final ProductService productService;
    private final Appointment appointment = Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build();
    private final Visit visit = Visit.builder().status(VisitStatusEnum.CONSULT).appointment(appointment).build();

    @ModelAttribute("lineItems")
    public List<LineItem> lineItems() {
        return new ArrayList<>();
    }

    @GetMapping("/price/sell")
    String setupProductSell_InitialCall(Model model, SessionStatus status) {
        status.setComplete();

        updateModel(model, new ArrayList<>());
        return "salesconsult-generic-module/productpage";
    }

    @PostMapping("/price/sell/lineitem")
    String foundProductAddLineItemViaHtmx(@NotNull Long inputProductId, @NotNull BigDecimal inputProductQuantity,
                                          @ProjectedPayload @ModelAttribute(value = "lineItems") List<LineItem> lineItems, Model model) {
        List<LineItem> list = lineItemService.createPricing(inputProductId, inputProductQuantity);
        AtomicLong counter = new AtomicLong(lineItems.size() + 1);
        list.forEach((lineItem -> lineItem.setId(counter.getAndIncrement())));
        lineItems.addAll(list);

        updateModel(model, lineItems);
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }

    @DeleteMapping("/price/sell/lineitem/{lineItemId}")
    String deleteProductFromLineItemsViaHtmx(@NotNull @PathVariable Long lineItemId, @ModelAttribute("lineItems") List<LineItem> lineItems, Model model) {
        updateModel(model, lineItems.stream().filter(lineitem -> !lineitem.getId().equals(lineItemId)).collect(Collectors.toList()));
        return "sales-module/fragments/htmx/lineitemsfulltable";
    }

    private Model updateModel(Model model, List<LineItem> lineItems) {
        ModelHelper.updateLineItemsInModel(model, lineItems);
        model
                .addAttribute("categoryNames", productService.getCategories())
                .addAttribute("productSearchUrl", SALES_PRICE_SELL)
                .addAttribute("costingSearchForm", new ProductController.ListForm(null, null, Boolean.FALSE))
                .addAttribute("visit", visit)
                .addAttribute("appointment", appointment)
                .addAttribute("salesType", SalesType.PRICE_INFO);
        return model;
    }
}