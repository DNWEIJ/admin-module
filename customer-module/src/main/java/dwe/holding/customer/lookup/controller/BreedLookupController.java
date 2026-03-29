package dwe.holding.customer.lookup.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.lookup.LookupBreeds;
import dwe.holding.customer.client.model.lookup.LookupSpecies;
import dwe.holding.customer.client.repository.LookupBreedsRepository;
import dwe.holding.customer.lookup.repository.SpeciesLookupRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
@Slf4j
@AllArgsConstructor
public class BreedLookupController {
    private final LookupBreedsRepository breedLookupRepository;
    private final SpeciesLookupRepository speciesLookupRepository;


    @GetMapping("lookup/breeds")
    String list(Model model) {
        model.addAttribute("breeds", breedLookupRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L)));
        model.addAttribute("activeMenu", "breeds");
        return "customer-module/lookup/breeds/list";
    }

    @GetMapping("lookup/breed")
    String newRecord(Model model) {
        model.addAttribute("breed", LookupBreeds.builder().species(new LookupSpecies()).build());
        model.addAttribute("species", speciesLookupRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L)).stream()
                .map(sp -> new PresentationElement(sp.getId(), sp.getSpecies() )).toList());
        model.addAttribute("activeMenu", "breeds");
        return "customer-module/lookup/breeds/action";
    }

    @GetMapping("lookup/breed/{breedId}")
    String editRecord(@PathVariable Long breedId, Model model) {
        LookupBreeds Breed = breedLookupRepository.findById(breedId).orElseThrow();
        model.addAttribute("breed", Breed.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ? Breed : new LookupBreeds());
        model.addAttribute("species", speciesLookupRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L)).stream()
                .map(sp -> new PresentationElement(sp.getId(), sp.getSpecies() )).toList());

        model.addAttribute("activeMenu", "breeds");

        return "customer-module/lookup/breeds/action";
    }

    @PostMapping("lookup/breed")
    @Transactional
    String saveRecord(LookupBreeds breed, RedirectAttributes redirect) {
        if (breed.isNew()) {
            breedLookupRepository.save(
                    LookupBreeds.builder()
                            .breed(breed.getBreed())
                            .build()
            );
        } else {
            LookupBreeds saveBreed = breedLookupRepository.findById(breed.getId()).orElseThrow();
            if (saveBreed.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                saveBreed.setBreed(breed.getBreed());
                breedLookupRepository.save(saveBreed);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/breed";
    }
}