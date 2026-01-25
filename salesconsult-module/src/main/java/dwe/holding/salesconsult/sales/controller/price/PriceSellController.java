package dwe.holding.salesconsult.sales.controller.price;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
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

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
@SessionAttributes("lineItems")
public class PriceSellController {
    public static final String SALES_PRICE_SELL = "/sales/price/sell/";
    private final LineItemService lineItemService;
    private final CostingService costingService;
    private final Appointment appointment = Appointment.builder().cancelled(YesNoEnum.No).completed(YesNoEnum.No).build();

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

    @GetMapping("/price/sell/more")
    String setupProductSell_RepeatableCall(Model model) {
        updateModel(model, new ArrayList<>());
        return "salesconsult-generic-module/productpage";
    }

    @PostMapping("/price/sell")
    String foundProductAddLineItemViaHtmx(@NotNull Long inputCostingId, @NotNull BigDecimal inputCostingQuantity,
                                          @ProjectedPayload @ModelAttribute(value = "lineItems") List<LineItem> lineItems, Model model) {
        List<LineItem> list = lineItemService.createPricing(inputCostingId, inputCostingQuantity);
        AtomicLong counter = new AtomicLong(lineItems.size() + 1);
        list.forEach((lineItem -> lineItem.setId(counter.getAndIncrement())));

        lineItems.addAll(list);
        updateModel(model, lineItems);
        model
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("url", SALES_PRICE_SELL);

        return "sales-module/fragments/htmx/lineitemsoverview";
    }

    @DeleteMapping("/price/sell/{lineItemId}")
    String foundProductAddLineItemViaHtmx(@NotNull @PathVariable Long lineItemId, @ModelAttribute("lineItems") List<LineItem> lineItems, Model model) {
        model.addAttribute("lineItems", lineItems.stream().filter(lineitem -> !lineitem.getId().equals(lineItemId)).toList());
        return "redirect:/sales/price/sell/more";
    }

    private Model updateModel(Model model, List<LineItem> lineItems) {
        ModelHelper.updateLineItemsInModel(model, lineItems);
        model
                .addAttribute("appointment", appointment)
                .addAttribute("salesType", SalesType.PRICE_INFO);
        return model;
    }
}