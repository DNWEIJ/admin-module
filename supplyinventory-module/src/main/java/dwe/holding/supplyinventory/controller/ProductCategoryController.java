package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.supplyinventory.model.LookupCostingCategory;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductCategoryController {
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;


    @GetMapping("/category/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("categories", lookupCostingCategoryRepository.findAll());
        return "supplies-module/product/category/list";
    }

    @GetMapping("/category")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("category", new LookupCostingCategory());
        return "supplies-module/product/category/list";
    }

    @GetMapping("/category/{id}")
    String editScreen(Model model, @PathVariable  Long id) {
        model.addAttribute("action", "Edit");
        model.addAttribute("category",
                lookupCostingCategoryRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElseThrow());
        return "supplies-module/product/category/action";
    }
}
