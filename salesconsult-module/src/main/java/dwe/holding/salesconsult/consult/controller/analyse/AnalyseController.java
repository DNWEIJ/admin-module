package dwe.holding.salesconsult.consult.controller.analyse;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Analyse;
import dwe.holding.salesconsult.consult.model.AnalyseDescription;
import dwe.holding.salesconsult.consult.repository.AnalyseDescriptionRepository;
import dwe.holding.salesconsult.consult.repository.AnalyseRepository;
import dwe.holding.salesconsult.consult.service.AnalyseService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@RequestMapping("/admin")
@AllArgsConstructor
@Controller

public class AnalyseController {

    private final AnalyseRepository analyseRepository;
    private final AnalyseDescriptionRepository analyseDescriptionRepository;
    private final ProductRepository productRepository;
    private final AnalyseService analyseService;

    @GetMapping("analyse/description/{id}")
    String getList(@PathVariable Long id, Model model) {
        updateModel(model, id);
        return "sales-module/analyse/analyse/list";
    }

    @PostMapping("analysedescription/{analyseDescriptionId}/analyse/lineitem")
    String saveHtmxAnalyseItem(@PathVariable Long analyseDescriptionId, @NotNull Long inputCostingId, @NotNull BigDecimal inputCostingQuantity, Model model) {
        Product product = productRepository.findByIdAndMemberId(inputCostingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        AnalyseDescription desc = analyseDescriptionRepository.findByIdAndMemberId(analyseDescriptionId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        analyseRepository.save(
                Analyse.builder()
                        .analyseDescription(desc)
                        .product(product)
                        .quantity(inputCostingQuantity)
                        .lookupProductCategoryId(product.getLookupProductCategory().getId())
                        .build()
        );
        updateModel(model, desc.getId());
        return "sales-module/analyse/analyse/analysetable";
    }

    @PostMapping("/analyse/delete")
    String deleteRecords(@RequestParam(required = false) ArrayList<Long> analyseSelected, @NotNull Long analyseDescriptionId) {
        analyseService.delete(analyseSelected);
        return "redirect:/admin/analyse/description/" + analyseDescriptionId;
    }

    void updateModel(Model model, Long id) {
        model
                .addAttribute("analyses", analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), id))
                .addAttribute("salesType", SalesType.PRICE_INFO)
                .addAttribute("analyseDescriptionId", id)
                .addAttribute("productSearchUrl", "/admin/analysedescription/" + id + "/analyse");
    }
}
