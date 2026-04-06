package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductAndPricingPartialController {

    private final ProductRepository productRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;


    @GetMapping("/product/{productId}/partialedit/{type}")
    String readProductHtmx(Model model, @PathVariable Long productId, @PathVariable String type, ProductController.ListForm costingSearchForm) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(productId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchForm", costingSearchForm)
        ;
        return type.equals("pricing") ? "supplies-module/product/htmx/pricingsbody::editableTR" : "/supplies-module/product/htmx/productsbody::editableTR";
    }

    @GetMapping("/product/{costingId}/partialcancel/{type}")
    String cancelProductHtmx(Model model, @PathVariable Long costingId, @PathVariable String type) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(costingId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
        ;
        LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
        model
                .addAttribute("taxGoodPercentage", taxes.getTaxLow())
                .addAttribute("taxServicePercentage", taxes.getTaxHigh())
                .addAttribute("isFormHere", true)
        ;
        return type.equals("pricing") ? "supplies-module/product/htmx/pricingsbody::readonlyTR" : "/supplies-module/product/htmx/productsbody::readonlyTR";
    }

    @PostMapping("/product/{costingId}/partialsave/{type}")
    String updateProductHtmx(Product productForm, Model model, @PathVariable Long costingId, @PathVariable String type) {
        if (!costingId.equals(productForm.getId())) throw new IllegalArgumentException("costingId must be equals to costingId");

        Product product = productRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        if (type.equals("pricing")) {
            product.setUplift(productForm.getUplift());
            product.setSalesPriceExTax(productForm.getSalesPriceExTax());
            product.setProcessingFeeExTax(productForm.getProcessingFeeExTax());
            product.setTaxed(productForm.getTaxed());
        } else {
            product.setNomenclature(productForm.getNomenclature());
            product.setLookupProductCategory(lookupProductCategoryRepository.findById(productForm.getLookupProductCategory().getId()).orElseThrow());
            product.setShortCode(productForm.getShortCode());
            product.setBarcode(productForm.getBarcode());
            product.setHasBatchNr(productForm.getHasBatchNr());
            product.setHasSpillage(productForm.getHasSpillage());
        }
        Product saved = productRepository.save(product);
        return cancelProductHtmx(model, saved.getId(), type);
    }


    /*
            searchCosting search via Dropdown, showing all belonging products -> url defined in ProductController   .addAttribute("costingSearchUrl", "/product/search/product")
            searchCosting search via typing, showing the selected product in edit mode
     */
    @PostMapping(value = {"/product/lineitem", "/search/product"})
    String userSelectedGetProductsHtmx(Model model, ProductController.ListForm form, @RequestHeader(value = "HX-Current-URL", required = false) String parentCallingUrl) {
        if (form.inputCostingId() == null && form.categoryId() == null) {
            model.addAttribute("products", List.of());
        } else {
            if (form.inputCostingId() != null) {
                model.addAttribute("products", productRepository.findByIdAndMemberIdToDto(form.inputCostingId(), AutorisationUtils.getCurrentUserMid()));
            }
            if (form.categoryId() != null) {
                model.addAttribute("products", productRepository.findAllByLookupProductCategory_IdAndMemberIdOrderByNomenclatureToDto(form.categoryId(), AutorisationUtils.getCurrentUserMid()));
            }
        }
        LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
        model
                .addAttribute("taxGoodPercentage", taxes.getTaxLow())
                .addAttribute("taxServicePercentage", taxes.getTaxHigh())
                .addAttribute("costingSearchForm", ProductController.getListForm(form))
        ;
        return
                parentCallingUrl.contains("pricing") ?
                        "supplies-module/product/htmx/pricingsbody" :
                        parentCallingUrl.contains("inventory") ?
                                "supplies-module/product/inventory/inventorybody" : "supplies-module/product/htmx/productsbody";

    }
}
