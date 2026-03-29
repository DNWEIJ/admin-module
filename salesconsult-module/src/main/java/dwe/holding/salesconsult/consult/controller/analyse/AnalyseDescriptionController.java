package dwe.holding.salesconsult.consult.controller.analyse;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.AnalyseDescription;
import dwe.holding.salesconsult.consult.repository.AnalyseDescriptionRepository;
import dwe.holding.salesconsult.consult.repository.AnalyseItemRepository;
import dwe.holding.salesconsult.consult.repository.AnalyseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@AllArgsConstructor
@Controller

public class AnalyseDescriptionController {
    private final AnalyseItemRepository analyseItemRepository;
    private final AnalyseRepository analyseRepository;
    private final AnalyseDescriptionRepository analyseDescriptionRepository;

    @GetMapping("analyse/description/list")
    String getList(Model model) {
        model.addAttribute("analyses", analyseDescriptionRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()));
        return "sales-module/analyse/description/list";
    }

    @GetMapping("/analyse/description")
    String newRecord( Model model) {
        return getList(model);
    }

    @PostMapping("/analyse/description")
    String updateRecord(Long id, String description, Model model) {
        AnalyseDescription analyseDescription = analyseDescriptionRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElse(new  AnalyseDescription());
        analyseDescription.setDescription(description);
        analyseDescriptionRepository.save(analyseDescription);
        return getList(model);
    }
}
