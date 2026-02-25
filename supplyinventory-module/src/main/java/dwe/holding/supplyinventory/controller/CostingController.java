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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/costing")
public class CostingController {
    private final CostingRepository costingRepository;
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;

    /*** LIST ***/
    @GetMapping("/product")
    String showListPage(Model model) {
        model
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("costingSearchUrl", "/costing/product")
                .addAttribute("salesType", new SalesTypeDummy())
        ;
        return "supplies-module/product/list";
    }

    @PostMapping("/product/lineitem")
    String userSelectedGetProductsHtmx(Model model, ListForm form) {
        if (form.inputCostingId != null) {
            model.addAttribute("products", costingRepository.findByIdAndMemberIdToDto(form.inputCostingId, AutorisationUtils.getCurrentUserMid()));
        }
        if (form.categoryId != null) {
            model.addAttribute("products", costingRepository.findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclatureToDto(form.categoryId, AutorisationUtils.getCurrentUserMid()));
        }
        return "supplies-module/product/htmx/productsbody";
    }


    /*** SINGLE RECORD ***/
        @GetMapping("/product/{costingId}")
    String initialPag(Model model, @PathVariable Long costingId) {
        Costing product = costingId == 0 ? new Costing() : costingRepository.findById(costingId).get();
        model
                .addAttribute("product", product)
                .addAttribute("categoryId", product.getLookupCostingCategory().getId())
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("taxTypes", TaxTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new SalesTypeDummy())
        ;
        return "supplies-module/product/action";
    }


    public record ListForm(Long inputCostingId, Long categoryId) {
    }

    public record SalesTypeDummy() {
        public boolean isPriceInfo() {
            return false;
        }

        public boolean isEstimate() {
            return false;
        }

        public boolean isVisit() {
            return false;
        }

        public boolean isProduct() {
            return true;
        }
    }
}