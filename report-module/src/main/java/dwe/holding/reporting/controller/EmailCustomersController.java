package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

@Controller
@AllArgsConstructor
@RequestMapping("report")
public class EmailCustomersController {
    private final LookupSpeciesRepository lookupSpeciesRepository;

    @GetMapping("email")
    String setupMail(Model model, EmailForm emailForm) {
        if (emailForm != null && emailForm.nrOfMonths() == null) {
            emailForm = new EmailForm(24, "doNotCare", "doNotCare", "doNotCare","doNotCare",
                    0, List.of(), "doNotCare", true, 0, 0, true, 0, List.of())
            ;
        }
        model
                .addAttribute("emailForm", emailForm)
                .addAttribute("monthList", IntStream.rangeClosed(1, 24).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList())
                .addAttribute("ynvaluesList", YesNoEnum.getWebListDoNotCare())
                .addAttribute("sexTypeList", SexTypeEnum.getWebListDoNotCare())
                .addAttribute("species", lookupSpeciesRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L))
                        .stream().map(f -> new PresentationElement(f.getId(), f.getSpecies()))
                        .sorted(comparing(PresentationElement::getName)).toList())
                .addAttribute("customers", List.of())

        ;

        return "reporting-module/customeremail";
    }
    record EmailForm(Integer nrOfMonths, String onlyActiveWithLivePet,String onlyIncludeNobirthday, String emailAddress, String newsLetter,
                     Integer speciesListLength, List<String> species,
                     String sexType, Boolean onlyIncludeAgePet, Integer minAgePet, Integer maxAgePet, Boolean includeNoBirthdate,
                     Integer diagnosesLength, List<String> diagnoses
    ) {
    }
}
