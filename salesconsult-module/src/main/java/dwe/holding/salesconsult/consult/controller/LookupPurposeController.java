package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.lookup.LookupPurpose;
import dwe.holding.salesconsult.consult.repository.LookupPurposeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
@Slf4j
@AllArgsConstructor
public class LookupPurposeController {
    private final LookupPurposeRepository lookupPurposeRepository;

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
    @CacheEvict("purpose")
    public String saveRecord(LookupPurpose formPurpose, RedirectAttributes redirect) {
        if (formPurpose.isNew()) {
            lookupPurposeRepository.save(
                    LookupPurpose.builder()
                            .definedPurpose(formPurpose.getDefinedPurpose())
                            .timeInMinutes(formPurpose.getTimeInMinutes())
                            .build()
            );
        } else {
            LookupPurpose lookupPurpose = lookupPurposeRepository.findById(formPurpose.getId()).orElseThrow();
            if (lookupPurpose.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ) {
                lookupPurpose.setDefinedPurpose(formPurpose.getDefinedPurpose());
                lookupPurpose.setTimeInMinutes(formPurpose.getTimeInMinutes());
                lookupPurposeRepository.save(lookupPurpose);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/purposes";
    }
}