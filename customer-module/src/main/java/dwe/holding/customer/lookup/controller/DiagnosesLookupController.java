package dwe.holding.customer.lookup.controller;

import dwe.holding.customer.client.model.lookup.LookupDiagnose;
import dwe.holding.customer.lookup.repository.DiagnosesLookupRepository;
import dwe.holding.admin.security.AutorisationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
public class DiagnosesLookupController {
    private final DiagnosesLookupRepository diagnosesLookupRepository;

    public DiagnosesLookupController(DiagnosesLookupRepository diagnosesLookupRepository) {
        this.diagnosesLookupRepository = diagnosesLookupRepository;
    }

    @GetMapping("lookup/diagnosis")
    String list(Model model) {
        model.addAttribute("diagnoses", diagnosesLookupRepository.getByMemberId(AutorisationUtils.getCurrentUserMid()));
        model.addAttribute("activeMenu", "diagnosis");
        return "customer-module/lookup/diagnosis/list";
    }

    @GetMapping("lookup/diagnose")
    String newRecord(Model model) {
        model.addAttribute("lookupDiagnosis", new LookupDiagnose());
        model.addAttribute("activeMenu", "diagnosis");
        return "customer-module/lookup/diagnosis/action";
    }

    @GetMapping("lookup/diagnose/{diagnosesId}")
    String editRecord(@PathVariable Long diagnosesId, Model model) {
        LookupDiagnose diagnoses = diagnosesLookupRepository.findById(diagnosesId).orElseThrow();
        model.addAttribute("lookupDiagnosis", diagnoses.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ? diagnoses : new LookupDiagnose());
        ; //AutorisationUtils.getCurrentUserMid()
        model.addAttribute("activeMenu", "diagnosis");
        return "customer-module/lookup/diagnosis/action";
    }

    @PostMapping("lookup/diagnosis")
    @Transactional
    String saveRecord(LookupDiagnose formDiagnose, RedirectAttributes redirect) {
        if (formDiagnose.isNew()) {
            diagnosesLookupRepository.save(
                    LookupDiagnose.builder()
                            .nomenclature(formDiagnose.getNomenclature())
                            .build()
            );
        } else {
            LookupDiagnose diag = diagnosesLookupRepository.findById(formDiagnose.getId()).orElseThrow();
            if (diag.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                diag.setNomenclature(formDiagnose.getNomenclature());
                diagnosesLookupRepository.save(diag);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/diagnosis";
    }
}