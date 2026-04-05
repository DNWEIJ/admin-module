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
public class ProductInventoryController {
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;


    @GetMapping("/inventory")
    String showListPage(Model model, ProductController.ListForm form) {
        if (form.inputCostingId() == null && form.categoryId() == null)
            form = new ProductController.ListForm(null, null, Boolean.FALSE);
        model
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchUrl", "/product/product")
                .addAttribute("costingSearchForm", form)
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/inventory/list";
    }
//
//    @GetMapping("/category")
//    String newScreen(Model model) {
//        model.addAttribute("action", "Create");
//        model.addAttribute("category", new LookupCostingCategory());
//        return "supplies-module/product/category/action";
//    }
//
//
//    @PostMapping("/category")
//    String saveScreen(LookupCostingCategory category, RedirectAttributes redirect) {
//        if (category.getId() != null) {
//            LookupCostingCategory tobeSavedCategory = lookupCostingCategoryRepository.findById(category.getId()).orElseThrow();
//            tobeSavedCategory.setCategory(category.getCategory());
//            lookupCostingCategoryRepository.save(tobeSavedCategory);
//        } else {
//            LookupCostingCategory lookupCostingCategory = lookupCostingCategoryRepository.save(LookupCostingCategory.builder().category(category.getCategory()).build());
//
//        }
//        redirect.addFlashAttribute("message", "label.saved");
//        return "redirect:/product/categories";
//    }
//
//    @GetMapping("/category/{id}")
//    String editScreen(Model model, @PathVariable  Long id) {
//        model.addAttribute("action", "Edit");
//        model.addAttribute("category",
//                lookupCostingCategoryRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElseThrow());
//        return "supplies-module/product/category/action";
//    }
}
