package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.reporting.model.DocumentReportType;
import dwe.holding.reporting.model.ReportTemplate;
import dwe.holding.reporting.repository.DocumentTemplateRepository;
import dwe.holding.reporting.service.ReportingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/report")
@AllArgsConstructor
@Controller
public class TemplateMaintanceController {
    private final DocumentTemplateRepository documentTemplateRepository;
    private final ReportingService reportingService;

    @GetMapping("/templates")
    public String listTemplates(Model model) {
        model.addAttribute("templates", documentTemplateRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()));
        return "reporting-module/template/list";
    }

    @GetMapping("/template")
    public String newTemplate(Model model) {
        model
                .addAttribute("template", new ReportTemplate())
                .addAttribute("templateType", DocumentReportType.getWebList());
        return "reporting-module/template/action";
    }

    @GetMapping("/template/{id}")
    public String newTemplate(@PathVariable Long id, Model model) {
        model
                .addAttribute("template", documentTemplateRepository.findById(id).orElseThrow())
                .addAttribute("templateType", DocumentReportType.getWebList());
        return "reporting-module/template/action";
    }

    @PostMapping("/template")
    public String saveTemplate(Model model, ReportTemplate reportTemplate) {
        documentTemplateRepository.save(reportTemplate);
        return "redirect:/report/templates";
    }

    @DeleteMapping("/template/{templateId}")
    @Transactional
    public String saveTemplate(@PathVariable Long templateId, Model model) {
        // reportingService.deleteTemplate(templateId);
        documentTemplateRepository.deleteByIdAndMemberId(templateId, AutorisationUtils.getCurrentUserMid());
        model.addAttribute("templates", documentTemplateRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()));
        return "reporting-module/template/list::tableBody";
    }
}
