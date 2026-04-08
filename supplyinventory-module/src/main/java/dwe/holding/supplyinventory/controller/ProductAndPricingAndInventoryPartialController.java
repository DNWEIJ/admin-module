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
public class ProductAndPricingAndInventoryPartialController {

    public static final String PRICING = "pricing";
    public static final String INVENTORY = "inventory";
    public static final String PRODUCT = "product";
    private final ProductRepository productRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;


    @GetMapping("/product/{productId}/partialedit/{type}")
    String readProductHtmx(Model model, @PathVariable Long productId, @PathVariable String type, ProductController.ListForm costingSearchForm) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(productId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("locals", AutorisationUtils.getLocalMemberMapShort())
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchForm", costingSearchForm)
        ;
        return type.equals(PRICING) ? "supplies-module/product/htmx/pricingsbody::editableTR" :
                type.equals(INVENTORY) ? "supplies-module/product/inventory/inventorybody::editableTR" : "/supplies-module/product/htmx/productsbody::editableTR";
    }

    @GetMapping("/product/{costingId}/partialcancel/{type}")
    String cancelProductHtmx(Model model, @PathVariable Long costingId, @PathVariable String type) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(costingId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("locals", AutorisationUtils.getLocalMemberMapShort())
        ;
        LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
        model
                .addAttribute("taxGoodPercentage", taxes.getTaxLow())
                .addAttribute("taxServicePercentage", taxes.getTaxHigh())
                .addAttribute("isFormHere", true)
        ;
        return type.equals(PRICING) ? "supplies-module/product/htmx/pricingsbody::readonlyTR" :
                type.equals(INVENTORY) ? "supplies-module/product/inventory/inventorybody::readonlyTR" : "/supplies-module/product/htmx/productsbody::readonlyTR";
    }

    @PostMapping("/product/{costingId}/partialsave/{type}")
    String updateProductHtmx(Product productForm, Model model, @PathVariable Long costingId, @PathVariable String type) {
        if (!costingId.equals(productForm.getId())) throw new IllegalArgumentException("costingId must be equals to costingId");

        Product product = productRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        if (type.equals(PRICING)) {
            product.setUplift(productForm.getUplift());
            product.setSalesPriceExTax(productForm.getSalesPriceExTax());
            product.setProcessingFeeExTax(productForm.getProcessingFeeExTax());
            product.setTaxed(productForm.getTaxed());
        }
        if (type.equals(PRODUCT)) {
            product.setNomenclature(productForm.getNomenclature());
            product.setLookupProductCategory(lookupProductCategoryRepository.findById(productForm.getLookupProductCategory().getId()).orElseThrow());
            product.setShortCode(productForm.getShortCode());
            product.setBarcode(productForm.getBarcode());
            product.setHasBatchNr(productForm.getHasBatchNr());
            product.setHasSpillage(productForm.getHasSpillage());
        }
        if (type.equals(INVENTORY)) {
            // TODO:what to do?
        }
        Product saved = productRepository.save(product);
        return cancelProductHtmx(model, saved.getId(), type);
    }


    /*
            searchCosting search via Dropdown, showing all belonging products -> url defined in ProductController   .addAttribute("productSearchUrl", "/product/search/product")
            searchCosting search via typing, showing the selected product in edit mode
     */
    @PostMapping(value = {"/search/product/lineitem", "/search/product"})
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
                .addAttribute("locals", AutorisationUtils.getLocalMemberMapShort())
        ;
        return
                parentCallingUrl.contains(PRICING) ?
                        "supplies-module/product/htmx/pricingsbody" :
                        parentCallingUrl.contains(INVENTORY) ?
                                "supplies-module/product/inventory/inventorybody" : "supplies-module/product/htmx/productsbody";

    }
}
