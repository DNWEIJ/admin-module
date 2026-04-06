package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupProductCategory;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
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
    private final LookupProductCategoryRepository lookupProductCategoryRepository;


    @GetMapping("/categories")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("categories", lookupProductCategoryRepository.findAllWithConnection());
        return "supplies-module/product/category/list";
    }

    @GetMapping("/category")
    String newScreen(Model model) {
        model.addAttribute("action", "Create")
                .addAttribute("category", LookupProductCategory.builder().deleted(YesNoEnum.No).build())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
        ;
        return "supplies-module/product/category/action";
    }


    @PostMapping("/category")
    String saveScreen(LookupProductCategory categoryForm, RedirectAttributes redirect) {
        if (categoryForm.getId() != null) {
            LookupProductCategory tobeSavedCategory = lookupProductCategoryRepository.findById(categoryForm.getId()).orElseThrow();
            tobeSavedCategory.setCategoryName(categoryForm.getCategoryName());
            tobeSavedCategory.setDeleted(categoryForm.getDeleted());
            lookupProductCategoryRepository.save(tobeSavedCategory);
        } else {
            lookupProductCategoryRepository.save(
                    LookupProductCategory.builder().categoryName(categoryForm.getCategoryName()).deleted(categoryForm.getDeleted()).build()
            );
        }
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/product/categories";
    }

    @GetMapping("/category/{id}")
    String editScreen(Model model, @PathVariable Long id) {
        model.addAttribute("action", "Edit")
                .addAttribute("category", lookupProductCategoryRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElseThrow())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList());
        return "supplies-module/product/category/action";
    }
}
