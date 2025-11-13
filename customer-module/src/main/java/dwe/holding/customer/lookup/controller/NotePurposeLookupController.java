package dwe.holding.customer.lookup.controller;

import dwe.holding.customer.client.model.lookup.LookupNotePurpose;
import dwe.holding.customer.lookup.repository.NotePurposeLookupRepository;
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
public class NotePurposeLookupController {
    private final NotePurposeLookupRepository notePurposeLookupRepository;

    public NotePurposeLookupController(NotePurposeLookupRepository notePurposeLookupRepository) {
        this.notePurposeLookupRepository = notePurposeLookupRepository;
    }

    @GetMapping("lookup/notepurposes")
    String list(Model model) {
        model.addAttribute("notepurposes", notePurposeLookupRepository.getByMemberId(77L)); // todo replace with
        model.addAttribute("activeMenu", "notepurpose");
        return "customer-module/lookup/notepurposes/list";
    }

    @GetMapping("lookup/notepurpose")
    String newRecord(Model model) {
        model.addAttribute("notepurpose", new LookupNotePurpose());
        model.addAttribute("activeMenu", "notepurpose");
        return "customer-module/lookup/notepurposes/action";
    }

    @GetMapping("lookup/notepurpose/{notePurposeId}")
    String editRecord(@PathVariable Long notePurposeId, Model model) {
        LookupNotePurpose notePurposes = notePurposeLookupRepository.findById(notePurposeId).orElseThrow();
        model.addAttribute("notepurpose", notePurposes.getMemberId().equals(77L) ? notePurposes : new LookupNotePurpose());
        model.addAttribute("activeMenu", "notepurpose");
        ; //AutorisationUtils.getCurrentUserMid()
        return "customer-module/lookup/notepurposes/action";
    }

    @PostMapping("lookup/notepurpose")
    @Transactional
    String saveRecord(LookupNotePurpose formNotePurpose, RedirectAttributes redirect) {
        if (formNotePurpose.isNew()) {
            notePurposeLookupRepository.save(
                    LookupNotePurpose.builder()
                            .preDefinedPurpose(formNotePurpose.getPreDefinedPurpose())
                            .build()
            );
        } else {
            LookupNotePurpose notePurpose = notePurposeLookupRepository.findById(formNotePurpose.getId()).orElseThrow();
            if (notePurpose.getMemberId().equals(77L) ) { //AutorisationUtils.getCurrentUserMid())) {
                notePurpose.setPreDefinedPurpose(formNotePurpose.getPreDefinedPurpose());
                notePurposeLookupRepository.save(notePurpose);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/notepurposes";
    }
}