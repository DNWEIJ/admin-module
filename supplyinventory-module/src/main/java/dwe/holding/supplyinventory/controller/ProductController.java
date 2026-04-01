package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.LookupCostingCategory;
import dwe.holding.supplyinventory.repository.ProductRepository;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;

    /*** LIST ***/
    @GetMapping("/products")
    String showListPage(Model model, ListForm form) {
        if (form.inputCostingId() == null && form.categoryId() == null)
            form = new ListForm(null, null, Boolean.FALSE);
        model
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("salesType", new SalesTypeDummy())
                .addAttribute("costingSearchUrl", "/product/product")
                .addAttribute("costingSearchForm", form)
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/list";
    }

    /*** SINGLE NEW RECORD ***/
    @GetMapping("/product")
    String newProduct(Model model) {
        Costing product = Costing.builder().lookupCostingCategory(new LookupCostingCategory()).build();
        model
                .addAttribute("product", product)
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new SalesTypeDummy())
        ;
        return "supplies-module/product/action";
    }

    /*** SINGLE UPDATE RECORD ***/
    @GetMapping("/product/{costingId}")
    String initialPag(Model model, @PathVariable Long costingId, ListForm costingSearchForm) {
        Costing product = costingId == 0 ? new Costing() : productRepository.findById(costingId).get();
        model
                .addAttribute("product", product)
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new SalesTypeDummy())
                .addAttribute("costingSearchForm", costingSearchForm)
        ;
        return "supplies-module/product/action";
    }


    /* Is also used in        PricingCostingController     */
    @PostMapping("/product/lineitem")
    String userSelectedGetProductsHtmx(Model model, ListForm form, @RequestHeader(value = "HX-Current-URL", required = false) String currentUrl) {
        if (form.inputCostingId == null && form.categoryId() == null) {
            model.addAttribute("products", List.of());
        } else {
            if (form.inputCostingId != null) {
                model.addAttribute("products", productRepository.findByIdAndMemberIdToDto(form.inputCostingId, AutorisationUtils.getCurrentUserMid()));
            }
            if (form.categoryId != null) {
                model.addAttribute("products", productRepository.findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclatureToDto(form.categoryId, AutorisationUtils.getCurrentUserMid()));
            }
        }
        LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
        model
                .addAttribute("taxGoodPercentage", taxes.getTaxLow())
                .addAttribute("taxServicePercentage", taxes.getTaxHigh())
                .addAttribute("costingSearchForm", getListForm(form))
        ;
        return currentUrl.contains("pricing") ? "supplies-module/product/htmx/pricingsbody" : "supplies-module/product/htmx/productsbody";
    }

    ListForm getListForm(ListForm form) {
        return form.categoryId() != null ? new ListForm(null, form.categoryId, Boolean.TRUE) : new ListForm(form.inputCostingId, null, Boolean.FALSE);
    }

    public record ListForm(Long inputCostingId, Long categoryId, Boolean useDropDown) {
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

        public boolean isOtc() {
            return false;
        }

        public boolean isCostingProduct() {
            return true;
        }
    }
}