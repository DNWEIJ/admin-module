package dwe.holding.supplyinventory.controller.operational;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.supplyinventory.repository.ProductBatchNumberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/costing")
@AllArgsConstructor
public class ProductBatchNumberHtmxController {

    private final ProductBatchNumberRepository productBatchNumberRepository;

    @GetMapping("/costing/{costingId}/batchnumbers")
    String getBatchListHtmx(@PathVariable Long costingId, Model model) {
        model.addAttribute("batchList", productBatchNumberRepository.findByProductIdAndMemberIdAndLocalMemberIdAndEndDateIsNull(costingId, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid()));
        return "supplies-module/fragments/costing/batchlist";
    }
}