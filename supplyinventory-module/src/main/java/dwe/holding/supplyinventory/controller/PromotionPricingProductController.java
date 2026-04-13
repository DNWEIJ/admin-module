package dwe.holding.supplyinventory.controller;

import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.model.ProductPricePromotion;
import dwe.holding.supplyinventory.model.type.PricingTypeEnum;
import dwe.holding.supplyinventory.repository.ProductPricePromotionRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class PromotionPricingProductController {
    private final ProductRepository productRepository;
    private final ProductPricePromotionRepository productPricePromotionRepository;
    private final ProductAndPricingAndInventoryPartialController productAndPAndIController;

    @GetMapping("/product/{productId}/pricing/promotion")
    String showListPage(Model model, @PathVariable long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        model
                .addAttribute("product", product)
                .addAttribute("promotion",
                        ProductPricePromotion.builder()
                                .startDate(LocalDate.now()).endDate(LocalDate.now())
                                .productId(product.getId())
                                .buyXforY_X(0).buyXforY_Y(0)
                                .salesPriceExTax(BigDecimal.ZERO).processingFee(BigDecimal.ZERO)
                                .reductionPercentage(BigDecimal.ZERO)
                                .build())
        ;
        return "supplies-module/product/pricing/pricepromotion/action";
    }

    @PostMapping("/product/{productId}/pricing/promotion")
    String newRecord(@PathVariable Long productId, ProductPricePromotion form, Model model, HttpServletResponse response) {
        boolean success = validate(form);
        boolean hasOverlap = productPricePromotionRepository.hasOverlappingPromotions(productId, null, form.getStartDate(), form.getEndDate());

        if (!success || hasOverlap) {
            if (hasOverlap) {
                model.addAttribute("message", "pricing.error.dateoverlap");
            } else {
                model.addAttribute("message", "pricing.error.select");
            }
            model.addAttribute("messageClass", "alert-error");
            return "admin-module/modal/message::notification";
        }
        PricingTypeEnum pricingTypeEnum = null;
        if (isNonZero(form.getSalesPriceExTax()) || isNonZero(form.getProcessingFee())) pricingTypeEnum = PricingTypeEnum.SALESPRICE_PROCESINGFEE;
        if (isNonZero(form.getReductionPercentage())) pricingTypeEnum = PricingTypeEnum.REDUCTION_PERCENTAGE;
        if (form.getBuyXforY_X() > 0 && form.getBuyXforY_Y() > 0) pricingTypeEnum = PricingTypeEnum.TWO_FOR_THREE;

        Product product = productRepository.findById(productId).orElseThrow();

        productPricePromotionRepository.save(ProductPricePromotion.builder()
                .startDate(form.getStartDate()).endDate(form.getEndDate())
                .productId(product.getId())
                .buyXforY_X(form.getBuyXforY_X()).buyXforY_Y(form.getBuyXforY_Y())
                .salesPriceExTax(form.getSalesPriceExTax()).processingFee(form.getProcessingFee())
                .reductionPercentage(form.getReductionPercentage())
                .pricingType(pricingTypeEnum)
                .build());

        response.setHeader("HX-Trigger", "closeModal");
        String returnString = productAndPAndIController.cancelProductHtmx(model, product.getId(), ProductAndPricingAndInventoryPartialController.PRICING);
        model.addAttribute("isOOB", true);
        return returnString;
    }

    @GetMapping("/product/{productId}/pricing/promotion/{promotionId}")
    /**
     * We can have multiple promotion records. However they NEVER will be overlapping in time
     * So make sure to have the validation done before saving.
     * We will list old current and new ones, so the user as an insight
     */
    String getExistingRecord(@PathVariable Long productId, @PathVariable Long promotionId, Model model) {
        Product product = productRepository.findById(productId).orElseThrow();

        model.addAttribute("promotions", product.getPricePromotions())
                .addAttribute("product", product)
                .addAttribute("promotion", product.getPricePromotions().stream().filter(p -> p.getId().equals(promotionId)).findFirst().get())
        ;
        return "supplies-module/product/pricing/pricepromotion/action";
    }

    @PostMapping("/product/{productId}/pricing/promotion/{promotionId}")
    String saveRecord(@PathVariable Long productId, @PathVariable Long promotionId, ProductPricePromotion form, Model model, HttpServletResponse response) {
        boolean success = validate(form);
        boolean hasOverlap = productPricePromotionRepository.hasOverlappingPromotions(productId, promotionId, form.getStartDate(), form.getEndDate());
        if (!success || hasOverlap) {
            if (hasOverlap) {
                model.addAttribute("message", "pricing.error.dateoverlap");
            } else {
                model.addAttribute("message", "pricing.error.select");
            }
            model.addAttribute("messageClass", "alert-error");
            return "admin-module/modal/message::notification";
        }
        PricingTypeEnum pricingTypeEnum = null;
        if (isNonZero(form.getSalesPriceExTax()) || isNonZero(form.getProcessingFee())) pricingTypeEnum = PricingTypeEnum.SALESPRICE_PROCESINGFEE;
        if (isNonZero(form.getReductionPercentage())) pricingTypeEnum = PricingTypeEnum.REDUCTION_PERCENTAGE;
        if (form.getBuyXforY_X() > 0 && form.getBuyXforY_Y() > 0) pricingTypeEnum = PricingTypeEnum.TWO_FOR_THREE;

        Product product = productRepository.findById(productId).orElseThrow();
        ProductPricePromotion promo = product.getPricePromotions().stream().filter(p -> p.getId().equals(promotionId)).findFirst().get();
        promo.setStartDate(form.getStartDate());
        promo.setEndDate(form.getEndDate());
        promo.setSalesPriceExTax(form.getSalesPriceExTax());
        promo.setProcessingFee(form.getProcessingFee());
        promo.setReductionPercentage(form.getReductionPercentage());
        promo.setPricingType(pricingTypeEnum);
        promo.setBuyXforY_X(form.getBuyXforY_X());
        promo.setBuyXforY_Y(form.getBuyXforY_Y());
        promo.setProductId(product.getId());
        productPricePromotionRepository.save(promo);

        response.setHeader("HX-Trigger", "closeModal");
        String returnString = productAndPAndIController.cancelProductHtmx(model, product.getId(), ProductAndPricingAndInventoryPartialController.PRICING);
        model.addAttribute("isOOB", true);
        return returnString;
    }


    private boolean validate(ProductPricePromotion form) {

        boolean priceChange = isNonZero(form.getSalesPriceExTax()) || isNonZero(form.getProcessingFee());
        boolean percentage = isNonZero(form.getReductionPercentage());
        boolean xForY = form.getBuyXforY_X() > 0 && form.getBuyXforY_Y() > 0;

        return (priceChange && !percentage && !xForY)
                || (!priceChange && percentage && !xForY)
                || (!priceChange && !percentage && xForY);
    }

    private boolean isNonZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) != 0;
    }
}