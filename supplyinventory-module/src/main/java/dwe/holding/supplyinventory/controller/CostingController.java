package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.type.TaxTypeEnum;
import dwe.holding.supplyinventory.repository.CostingRepository;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/costing")
public class CostingController {
    private final CostingRepository costingRepository;
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;

    @GetMapping("/product")
    String initialPag(Model model) {
        Costing product = costingRepository.findById(21296L).get();
        model
                .addAttribute("product", product)
                .addAttribute("categoryId", product.getLookupCostingCategory().getId())
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("taxTypes", TaxTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
        ;
        return "supplies-module/product/action";
    }
}
