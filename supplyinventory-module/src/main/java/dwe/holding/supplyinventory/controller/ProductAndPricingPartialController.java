package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.repository.ProductRepository;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductAndPricingPartialController {

    private final ProductRepository productRepository;
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;


    @GetMapping("/product/{productId}/partialedit/{type}")
    String readProductHtmx(Model model, @PathVariable Long productId, @PathVariable String type, ProductController.ListForm costingSearchForm) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(productId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
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
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
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
    String updateProductHtmx(Costing productForm, Model model, @PathVariable Long costingId, @PathVariable String type) {
        if (!costingId.equals(productForm.getId())) throw new IllegalArgumentException("costingId must be equals to costingId");

        Costing product = productRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        if (type.equals("pricing")) {
            product.setUplift(productForm.getUplift());
            product.setSalesPriceExTax(productForm.getSalesPriceExTax());
            product.setProcessingFeeExTax(productForm.getProcessingFeeExTax());
            product.setTaxed(productForm.getTaxed());
        } else {
            product.setNomenclature(productForm.getNomenclature());
            product.setLookupCostingCategory(lookupCostingCategoryRepository.findById(productForm.getLookupCostingCategory().getId()).orElseThrow());
            product.setShortCode(productForm.getShortCode());
            product.setBarcode(productForm.getBarcode());
            product.setHasBatchNr(productForm.getHasBatchNr());
            product.setHasSpillage(productForm.getHasSpillage());
        }
        Costing saved = productRepository.save(product);
        return cancelProductHtmx(model, saved.getId(), type);
    }
}
