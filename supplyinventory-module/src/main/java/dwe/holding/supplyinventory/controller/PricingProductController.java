package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class PricingProductController {
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;

    /**
     * Via
     *   CostingController::userSelectedGetProductsHtmx(Model model, ListForm form) {
     *   The list of products is created
     */
    /*** LIST ***/
    @GetMapping("/pricing")
    String showListPage(Model model) {
        model
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchUrl", "/product/product")
                .addAttribute("costingSearchForm",  new ProductController.ListForm(null,null, Boolean.TRUE))
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/pricing/list";
    }
}