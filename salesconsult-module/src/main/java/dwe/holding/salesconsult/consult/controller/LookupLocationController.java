package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.LookupLocation;
import dwe.holding.salesconsult.consult.repository.LookupLocationRepository;
import dwe.holding.shared.model.type.YesNoEnum;
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
@RequestMapping("/customer")
@Slf4j
public class LookupLocationController {
    private final LookupLocationRepository lookupLocationRepository;

    public LookupLocationController(LookupLocationRepository lookupLocationRepository) {
        this.lookupLocationRepository = lookupLocationRepository;
    }

    @GetMapping("lookup/locations")
    String list(Model model) {
        model.addAttribute("locations", lookupLocationRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()));
        model.addAttribute("activeMenu", "location");
        return "customer-module/lookup/location/list";
    }

    @GetMapping("lookup/location")
    String newRecord(Model model) {
        model.addAttribute("location", new LookupLocation())
                .addAttribute("activeMenu", "location")
                .addAttribute("yesNoList", YesNoEnum.getWebList());

        return "customer-module/lookup/location/action";
    }

    @GetMapping("lookup/location/{locationsId}")
    String editRecord(@PathVariable Long locationsId, Model model) {
        LookupLocation locations = lookupLocationRepository.findById(locationsId).orElseThrow();
        model.addAttribute("location", locations.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ? locations : new LookupLocation())
                .addAttribute("activeMenu", "location")
                .addAttribute("yesNoList", YesNoEnum.getWebList());
        return "customer-module/lookup/location/action";
    }

    @PostMapping("lookup/location")
    @Transactional
    String saveRecord(LookupLocation formLocation, RedirectAttributes redirect) {
        if (formLocation.isNew()) {
            lookupLocationRepository.save(
                    LookupLocation.builder()
                            .nomenclature(formLocation.getNomenclature())
                            .build()
            );
        } else {
            LookupLocation diag = lookupLocationRepository.findById(formLocation.getId()).orElseThrow();
            if (diag.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                diag.setNomenclature(formLocation.getNomenclature());
                lookupLocationRepository.save(diag);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/locations";
    }
}