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
    String readCostingLineHtmx(Model model, @PathVariable Long productId, @PathVariable String type, ProductController.ListForm costingSearchForm) {
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
    String cancelCostingLineHtmx(Model model, @PathVariable Long costingId, @PathVariable String type) {
        model
                .addAttribute("product", productRepository.findByIdAndMemberIdToDto(costingId, AutorisationUtils.getCurrentUserMid()))
                .addAttribute("categories", lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1))
                        .stream().map(loocategory -> new PresentationElement(loocategory.getId(), loocategory.getCategory())).toList())
        ;
        LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
        model
                .addAttribute("taxGoodPercentage", taxes.getTaxLow())
                .addAttribute("taxServicePercentage", taxes.getTaxHigh())
        ;
        return type.equals("pricing") ? "supplies-module/product/htmx/pricingsbody::readonlyTR" : "/supplies-module/product/htmx/productsbody::readonlyTR";
    }

    @PostMapping("/product/{costingId}/partialsave/{type}")
    String updateCostingLineHtmx(Costing costing, Model model, @PathVariable Long costingId, @PathVariable String type) {
        if (!costingId.equals(costing.getId())) throw new IllegalArgumentException("costingId must be equals to costingId");

        Costing product = productRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        if (type.equals("pricing")) {
            product.setUplift(costing.getUplift());
            product.setSalesPriceExTax(costing.getSalesPriceExTax());
            product.setProcessingFeeExTax(costing.getProcessingFeeExTax());
            product.setTaxed(costing.getTaxed());
        } else {
            product.setNomenclature(costing.getNomenclature());
            product.setLookupCostingCategory(costing.getLookupCostingCategory());
            product.setShortCode(costing.getShortCode());
            product.setBarcode(costing.getBarcode());
            product.setHasBatchNr(costing.getHasBatchNr());
            product.setHasSpillage(costing.getHasSpillage());
        }
        Costing saved = productRepository.save(product);
        return cancelCostingLineHtmx(model, saved.getId(), type);
    }
}
