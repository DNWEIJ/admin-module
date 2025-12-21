package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.model.lookup.LookupPurpose;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
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
public class LookupPurposeController {
    private final LookupPurposeRepository lookupPurposeRepository;

    public LookupPurposeController(LookupPurposeRepository lookupPurposeRepository) {
        this.lookupPurposeRepository = lookupPurposeRepository;
    }

    @GetMapping("lookup/purposes")
    String list(Model model) {
        model.addAttribute("purposes", lookupPurposeRepository.getByMemberIdOrderByDefinedPurpose(AutorisationUtils.getCurrentUserMid()));
        model.addAttribute("activeMenu", "purpose");
        return "customer-module/lookup/purposes/list";
    }

    @GetMapping("lookup/purpose")
    String newRecord(Model model) {
        model.addAttribute("purpose", new LookupPurpose());
        model.addAttribute("activeMenu", "purpose");
        return "customer-module/lookup/purposes/action";
    }

    @GetMapping("lookup/purpose/{notePurposeId}")
    String editRecord(@PathVariable Long notePurposeId, Model model) {
        LookupPurpose notePurposes = lookupPurposeRepository.findById(notePurposeId).orElseThrow();
        model.addAttribute("purpose", notePurposes.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ? notePurposes : new LookupPurpose());
        model.addAttribute("activeMenu", "purpose");
        return "customer-module/lookup/purposes/action";
    }

    @PostMapping("lookup/purpose")
    @Transactional
    String saveRecord(LookupPurpose formNotePurpose, RedirectAttributes redirect) {
        if (formNotePurpose.isNew()) {
            lookupPurposeRepository.save(
                    LookupPurpose.builder()
                            .definedPurpose(formNotePurpose.getDefinedPurpose())
                            .timeInMinutes(formNotePurpose.getTimeInMinutes())
                            .build()
            );
        } else {
            LookupPurpose notePurpose = lookupPurposeRepository.findById(formNotePurpose.getId()).orElseThrow();
            if (notePurpose.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ) {
                notePurpose.setDefinedPurpose(formNotePurpose.getDefinedPurpose());
                notePurpose.setTimeInMinutes(formNotePurpose.getTimeInMinutes());
                lookupPurposeRepository.save(notePurpose);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/purposes";
    }
}