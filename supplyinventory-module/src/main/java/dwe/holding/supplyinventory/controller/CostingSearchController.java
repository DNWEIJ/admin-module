package dwe.holding.supplyinventory.controller;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupCostingCategory;
import dwe.holding.supplyinventory.model.projection.CostingProjection;
import dwe.holding.supplyinventory.repository.CostingRepository;
import dwe.holding.supplyinventory.repository.LookupCostingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/costing")
public class CostingSearchController {

    private final CostingRepository costingRepository;
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;

    private Map<Long, String> costing;

    @GetMapping("/costing/search/costing/dropdown")
    public String searchLookupCostingDropdown(Model model) {

        if (costing == null || costing.isEmpty()) {
            costing = lookupCostingCategoryRepository.findByMemberIdOrderByCategory(77L).stream().collect(Collectors.toMap(LookupCostingCategory::getId, LookupCostingCategory::getCategory));
        }
        model.addAttribute("lookupCostings", costing);
        return "supplies-module/fragments/costing/selectcosting";
    }

    @PostMapping("/costing/search/costing/dropdown/found/costing")
    public String searchCostingForDropDown(Long categoryId, Model model) {
        StringBuilder sb = new StringBuilder();
        costingRepository.findAllByLookupCostingCategory_IdAndMemberIdOrderByNomenclature(categoryId, 77L).forEach(costingProj -> { //AutorisationUtils.getCurrentUserMid()
            sb.append("<option data-has-batch=\"").append(costingProj.hasBatchNr().equals(YesNoEnum.Yes) ? "true" : "false")
                    .append("\" data-id=\"").append(costingProj.id())
                    .append("\" data-nomenclature=\"").append(costingProj.nomenclature())
                    .append("\">")
                    .append(costingProj.nomenclature())
                    .append("</option>");
        });
        model.addAttribute("flatData", sb.toString());
        return "fragments/elements/flatData";
    }


    @PostMapping("/search/costing")
    public String searchCustomerHtmx(Model model, String searchCriteria) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "");
            return "fragments/elements/flatData";
        }
        String escapedSearch = Pattern.quote(searchCriteria);
        Pattern pattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);

        List<CostingProjection> list;
        try {
            Long.parseLong(searchCriteria);
            list = costingRepository.getCostingProjectionsWhenSearchCriteriaIsNumeric(searchCriteria, 77L); // AutorisationUtils.getCurrentMemberId()
        } catch (NumberFormatException e) {
            list = costingRepository.getCostingOnNomenclature(searchCriteria, 77L); // AutorisationUtils.getCurrentMemberId()
        }
        model.addAttribute("flatData", wrap(list.stream().map(str -> getOption(str, pattern)).toList()));
        return "fragments/elements/flatData";
    }

    private String wrap(List<String> listCostings) {
        return (listCostings.size() > 0) ?
                "<ul style=\"max-height: 180px; overflow: auto;\">" + String.join("", listCostings) + "</ul>"
                : "<ul style=\"max-height: 180px; overflow: auto;\">No record found</ul>";
    }

    private String getOption(CostingProjection proj, Pattern pattern) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(proj.nomenclature());
        while (matcher.find()) {
            String match = matcher.group();
            matcher.appendReplacement(result, "<strong>" + match + "</strong>");
        }
        matcher.appendTail(result);
        return "<li data-id=" + proj.id()
                + " data-has-batch=" + (proj.hasBatchNr().equals(YesNoEnum.Yes))
                + " data-nomenclature=\"" + proj.nomenclature() + "\""
                + ">" + proj.nomenclature() + "</li>";
    }
}