package dwe.holding.customer.lookup.controller;

import dwe.holding.customer.client.model.lookup.LookupSpecies;
import dwe.holding.customer.lookup.repository.SpeciesLookupRepository;
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
public class SpeciesLookupController {
    private final SpeciesLookupRepository speciesLookupRepository;

    public SpeciesLookupController(SpeciesLookupRepository speciesLookupRepository) {
        this.speciesLookupRepository = speciesLookupRepository;
    }

    @GetMapping("lookup/species")
    String list(Model model) {
        model.addAttribute("species", speciesLookupRepository.getByMemberId(77L)); // todo replace with
        model.addAttribute("activeMenu", "specy");
        return "customer-module/lookup/species/list";
    }

    @GetMapping("lookup/specy")
    String newRecord(Model model) {
        model.addAttribute("specy", new LookupSpecies());
        model.addAttribute("activeMenu", "specy");
        return "customer-module/lookup/species/action";
    }

    @GetMapping("lookup/specy/{specyId}")
    String editRecord(@PathVariable Long specyId, Model model) {
        LookupSpecies species = speciesLookupRepository.findById(specyId).orElseThrow();
        model.addAttribute("specy", species.getMemberId().equals(77L) ? species : new LookupSpecies());
        model.addAttribute("activeMenu", "specy");
        // AutorisationUtils.getCurrentUserMid()
        return "customer-module/lookup/species/action";
    }

    @PostMapping("lookup/specy")
    @Transactional
    String saveRecord(LookupSpecies formSpecy, RedirectAttributes redirect) {
        if (formSpecy.isNew()) {
            speciesLookupRepository.save(
                    LookupSpecies.builder()
                            .species(formSpecy.getSpecies())
                            .build()
            );
        } else {
            LookupSpecies specy = speciesLookupRepository.findById(formSpecy.getId()).orElseThrow();
            if (specy.getMemberId().equals(77L) ) { //AutorisationUtils.getCurrentUserMid())) {
                specy.setSpecies(formSpecy.getSpecies());
                speciesLookupRepository.save(specy);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/species";
    }
}