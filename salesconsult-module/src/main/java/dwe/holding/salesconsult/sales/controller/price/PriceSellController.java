package dwe.holding.salesconsult.sales.controller.price;

import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.ModelHelper;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.supplyinventory.expose.CostingService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RequestMapping("/sales")
@Controller
@AllArgsConstructor
@SessionAttributes("lineItems")
public class PriceSellController {
    private final LineItemService lineItemService;
    private final CostingService costingService;

    @ModelAttribute("lineItems")
    public List<LineItem> lineItems() {
        return new ArrayList<>();
    }

    @GetMapping("/price/sell")
    String setupProductSell_InitialCall(Model model, @ModelAttribute("lineItems") List<LineItem> lineItems) {
        model
                .addAttribute("salesType", SalesType.PRICE_INFO)
                .addAttribute("lineItems", new ArrayList<>());
        return "sales-module/generic/productpage";
    }

    @GetMapping("/price/sell/more")
    String setupProductSell_RepeatableCall(Model model, @ModelAttribute("lineItems") List<LineItem> lineItems) {
        model
                .addAttribute("salesType", SalesType.PRICE_INFO)
                .addAttribute("lineItems", lineItems);
        return "sales-module/generic/productpage";
    }

    @PostMapping("/price/sell")
    String foundProductAddLineItemViaHtmx(@NotNull Long inputCostingId, @NotNull BigDecimal inputCostingAmount,
                                          @ModelAttribute(value = "lineItems") List<LineItem> lineItems, Model model) {
        List<LineItem> list = lineItemService.createPricing(inputCostingId, inputCostingAmount);
        AtomicLong counter = new AtomicLong(lineItems.size() + 1);
        list.forEach((lineItem -> lineItem.setId(counter.getAndIncrement())));

        lineItems.addAll(list);
        ModelHelper.updateLineItemsInModel(model, lineItems);
        model
                .addAttribute("salesType", SalesType.PRICE_INFO)
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("url", "/sales/price/sell/");

        return "sales-module/fragments/htmx/lineitemsoverview";
    }

    @DeleteMapping("/price/sell/{lineItemId}")
    String foundProductAddLineItemViaHtmx(@NotNull @PathVariable Long lineItemId, @ModelAttribute("lineItems") List<LineItem> lineItems, Model model) {
        model.addAttribute("lineItems", lineItems.stream().filter(lineitem -> !lineitem.getId().equals(lineItemId)).toList());
        return "redirect:/sales/price/sell/more";
    }
}