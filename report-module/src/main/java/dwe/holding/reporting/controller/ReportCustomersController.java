package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.reporting.repository.dsl.EntityListDsls;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

@Controller
@AllArgsConstructor
@RequestMapping("report")
public class ReportCustomersController {
    public static final String DO_NOT_CARE = "doNotCare";
    private final LookupSpeciesRepository lookupSpeciesRepository;
    private final EntityListDsls entityListDsls;

    @GetMapping("customerlist")
    String setupMail(Model model, DocForm docForm) {

        if (docForm != null && docForm.nrOfMonths() == null) {
            docForm = new DocForm(1, DO_NOT_CARE, 0, 0, DO_NOT_CARE, DO_NOT_CARE, DO_NOT_CARE,
                    DO_NOT_CARE, DO_NOT_CARE, List.of())
            ;
        }
        model
                .addAttribute("docForm", docForm)
                .addAttribute("monthList", IntStream.rangeClosed(1, 24).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList())
                .addAttribute("ynvaluesList", YesNoEnum.getWebListDoNotCare())
                .addAttribute("sexTypeList", SexTypeEnum.getWebListDoNotCare())
                .addAttribute("species", lookupSpeciesRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L))
                        .stream().map(f -> new PresentationElement(f.getId(), f.getSpecies()))
                        .sorted(comparing(PresentationElement::getName)).toList())
                .addAttribute("customers", (isThereInput(docForm)) ? entityListDsls.findCustomers(AutorisationUtils.getCurrentUserMid(), docForm) : List.of())
        ;

        return "reporting-module/customeremail";
    }

    @PostMapping("customerlist")
    String sendpMailPreview(Model model,  @RequestParam List<String> petRec, @RequestParam List<Long> selectedRec) {

        Set<Long> selectedSet = new HashSet<>(selectedRec);

        Map<Long, List<Long>> result = petRec.stream()
                .map(s -> s.split("_"))
                .filter(arr -> arr.length == 2)
                .filter(arr -> selectedSet.contains(Long.parseLong(arr[0])))
                .collect(Collectors.groupingBy(
                        arr -> Long.parseLong(arr[0]),
                        Collectors.mapping(
                                arr -> Long.parseLong(arr[1]),
                                Collectors.toList()
                        )
                ));
        return "";
    }

    public record DocForm(Integer nrOfMonths,
                          String certainPetAge, Integer minAgePetInMonths, Integer maxAgePetInMonths,
                          String customerActive, String emailAddress, String newsLetter,
                          String onlyLivingPets, String sexType, List<String> species) {
    }


    boolean isThereInput(DocForm docForm) {
        if (docForm.certainPetAge().equals(DO_NOT_CARE) &&
                docForm.customerActive().equals(DO_NOT_CARE) &&
                docForm.emailAddress().equals(DO_NOT_CARE) &&
                docForm.newsLetter().equals(DO_NOT_CARE) &&
                docForm.onlyLivingPets().equals(DO_NOT_CARE) &&
                docForm.sexType().equals(DO_NOT_CARE) &&
                (docForm.species() == null || docForm.species().isEmpty())
        ) return false;
        return true;
    }
}
