package dwe.holding.supplyinventory.controller;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
/**
 * This controller uses shared costingSearch via ProdctAndPricingPartialController with ProductController
 */
public class PricingProductController {
    private final LookupProductCategoryRepository lookupProductCategoryRepository;

    @GetMapping("/pricing")
    String showListPage(Model model) {
        model
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("productSearchUrl", "/product/search/product")
                .addAttribute("costingSearchForm", new ProductController.ListForm(null, null, Boolean.TRUE))
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/pricing/list";
    }
}