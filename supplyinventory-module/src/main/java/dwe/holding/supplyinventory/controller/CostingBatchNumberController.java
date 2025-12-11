package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.supplyinventory.repository.CostingBatchNumberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/costing")
@AllArgsConstructor
public class CostingBatchNumberController {

    private final CostingBatchNumberRepository costingBatchNumberRepository;

    @GetMapping("/costing/{costingId}/batchnumbers")
    String getBatchListHtmx(@PathVariable Long costingId, Model model) {
        model.addAttribute("batchList", costingBatchNumberRepository.findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNull(costingId, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid()));
        return "supplies-module/fragments/costing/batchlist";
    }
}