package dwe.holding.reporting.controller;

import dwe.holding.reporting.model.DocumentReportType;
import dwe.holding.reporting.repository.DocumentTemplateRepository;
import dwe.holding.reporting.service.SessionStorageReporting;
import dwe.holding.shared.model.frontend.PresentationElement;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/report")
/**
 * This is a generic editor page. It just handles the ckeditor
 * and revert back to the report controller to do the generation
 */
public class EditReportController {
    private final DocumentTemplateRepository documentTemplateRepository;
    private final SessionStorageReporting sessionStorageReporting;

    @GetMapping("/process/selectreport")
    String showReportSelection(Model model, @RequestParam DocumentReportType reportType) {
        model
                .addAttribute("reportList", documentTemplateRepository.findByReportType(reportType)
                        .stream().map(template -> new PresentationElement(template.getId(), template.getPurpose())).toList())
                .addAttribute("reportType", reportType)
                .addAttribute("callBackUrl", sessionStorageReporting.getReporting().getCallbackUrl());
        return "/reporting-module/process/editreport";
    }

    @GetMapping("/retrieve/selectedreport")
    String returnReportHtmx(Model model, Long templateId, DocumentReportType reportType, String submitButton) {
        if (submitButton != null && submitButton.equals("_back")) {
            return "redirect:/report/process/selectreport?reportType=" + reportType;
        }
        model
                .addAttribute("template", documentTemplateRepository.findById(templateId).orElseThrow())
                .addAttribute("reportList", documentTemplateRepository.findByReportType(reportType)
                        .stream().map(template -> new PresentationElement(template.getId(), template.getPurpose())).toList())
                .addAttribute("reportType", reportType)
                .addAttribute("callBackUrl", sessionStorageReporting.getReporting().getCallbackUrl());
        return "/reporting-module/process/editreport";
    }
}
