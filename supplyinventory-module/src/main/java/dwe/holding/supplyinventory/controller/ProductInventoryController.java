package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductInventoryController {
    private final LookupProductCategoryRepository lookupProductCategoryRepository;
    private final ProductRepository productRepository;

    @GetMapping("/inventory")
    String showListPage(Model model, ProductController.ListForm form) {
        if (form.inputCostingId() == null && form.categoryId() == null)
            form = new ProductController.ListForm(null, null, Boolean.FALSE);
        model
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchUrl", "/product/search/product")
                .addAttribute("costingSearchForm", form)
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/inventory/list";
    }

    // Called from product search; 1 product search
    @PostMapping("/inventory")
    String showRecord(Model model, Long inputCostingId){
        model.addAttribute("products", productRepository.findByIdAndMemberIdToDto(inputCostingId, AutorisationUtils.getCurrentUserMid()));
        return "supplies-module/product/inventory/inventorybody";
    }
}
