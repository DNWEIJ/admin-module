package dwe.holding.supplyinventory.controller;

import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.CostingPricePromotion;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class PromotionPricingProductController {
    private final ProductRepository productRepository;

    @GetMapping("/product/{productId}/pricing/promotion")
    String showListPage(Model model, @PathVariable long productId) {
        Costing product = productRepository.findById(productId).orElseThrow();
        model
                .addAttribute("product", product)
                .addAttribute("promotion", new CostingPricePromotion())
        ;
        return "supplies-module/product/pricepromotion/action";
    }
}