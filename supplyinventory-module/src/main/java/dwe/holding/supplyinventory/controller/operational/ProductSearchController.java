package dwe.holding.supplyinventory.controller.operational;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.projection.CostingProjection;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
@RequestMapping("/costing")
@Slf4j
public class ProductSearchController {

    private final ProductRepository productRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;

    @GetMapping("/costing/search/category/dropdown")
    public String searchLookupCostingDropdown(Model model) {
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("lookupCostings", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No));
        return "supplies-module/fragments/costing/selectcosting";
    }

    @PostMapping("/costing/search/costing/dropdown/found/costing")
    public String searchCostingForDropDown(Long categoryId, Model model) {
        StringBuilder sb = new StringBuilder();
        productRepository.findAllByLookupProductCategory_IdAndMemberIdOrderByNomenclature(categoryId, AutorisationUtils.getCurrentUserMid()).forEach(costingProj ->
                sb.append("<option data-has-batch=\"").append(costingProj.hasBatchNr().equals(YesNoEnum.Yes) ? "true" : "false")
                        .append("\" data-id=\"").append(costingProj.id())
                        .append("\" data-nomenclature=\"").append(costingProj.nomenclature())
                        .append("\">")
                        .append(costingProj.nomenclature())
                        .append("</option>"));
        model.addAttribute("flatData", sb.toString());
        return "fragments/elements/flatData";
    }


    @PostMapping("/search/costing")
    public String searchCustomerHtmx(Model model, String searchCriteria) {
        LocalDateTime now = LocalDateTime.now();
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "");
            return "fragments/elements/flatData";
        }
        String escapedSearch = Pattern.quote(searchCriteria);
        Pattern pattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);

        List<CostingProjection> list;
        try {
            Long.parseLong(searchCriteria);
            list = productRepository.getProductProjectionsWhenSearchCriteriaIsNumeric(searchCriteria, AutorisationUtils.getCurrentUserMid());
        } catch (NumberFormatException e) {
            list = productRepository.getProductOnNomenclature(searchCriteria, AutorisationUtils.getCurrentUserMid());
        }
        model.addAttribute("flatData", wrap(list.stream().map(str -> getOption(str, pattern)).toList()));
        log.info("searchCustomerHtmx::Spending time for lookup: " + Duration.between(now, LocalDateTime.now()));
        return "fragments/elements/flatData";
    }

    private String wrap(List<String> listCostings) {
        return (!listCostings.isEmpty()) ?
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