package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.supplyinventory.model.LookupCostingCategory;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductCategoryController {
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;


    @GetMapping("/categories")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("categories", lookupCostingCategoryRepository.findAll());
        return "supplies-module/product/category/list";
    }

    @GetMapping("/category")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("category", new LookupCostingCategory());
        return "supplies-module/product/category/action";
    }


    @PostMapping("/category")
    String saveScreen(LookupCostingCategory category, RedirectAttributes redirect) {
        if (category.getId() != null) {
            LookupCostingCategory tobeSavedCategory = lookupCostingCategoryRepository.findById(category.getId()).orElseThrow();
            tobeSavedCategory.setCategory(category.getCategory());
            lookupCostingCategoryRepository.save(tobeSavedCategory);
        } else {
            LookupCostingCategory lookupCostingCategory = lookupCostingCategoryRepository.save(LookupCostingCategory.builder().category(category.getCategory()).build());

        }
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/product/categories";
    }

    @GetMapping("/category/{id}")
    String editScreen(Model model, @PathVariable  Long id) {
        model.addAttribute("action", "Edit");
        model.addAttribute("category",
                lookupCostingCategoryRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElseThrow());
        return "supplies-module/product/category/action";
    }
}
