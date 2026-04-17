package dwe.holding.supplyinventory.controller.operational;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.projection.ProductProjection;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductSearchController {

    private final ProductRepository productRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;

    @GetMapping("/product/searching/category/dropdown")
    public String searchLookupProductDropdown(Model model) {
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("lookupProductss", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No));
        return "supplies-module/fragments/costing/selectcosting";
    }

    @PostMapping("/product/searching/product/dropdown/found/product")
    public String searchProductForDropDown(Long categoryId, Model model) {
        StringBuilder sb = new StringBuilder();
        productRepository.findAllByLookupProductCategory_IdAndMemberIdOrderByNomenclature(categoryId, AutorisationUtils.getCurrentUserMid()).forEach(proj ->
                sb.append("<option data-has-batch=\"").append(proj.hasBatchNr().equals(YesNoEnum.Yes) ? "true" : "false")
                        .append("\" data-id=\"").append(proj.id())
                        .append("\" data-nomenclature=\"").append(proj.nomenclature())
                        .append("\">")
                        .append(proj.nomenclature())
                        .append("</option>"));
        model.addAttribute("flatData", sb.toString());
        return "fragments/elements/flatData";
    }


    @PostMapping("/searching/product")
    public String searchCustomerHtmx(Model model, String searchCriteria) {
        LocalDateTime now = LocalDateTime.now();
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            model.addAttribute("flatData", "");
            return "fragments/elements/flatData";
        }
        String escapedSearch = Pattern.quote(searchCriteria);
        Pattern pattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);

        List<ProductProjection> list;
        try {
            Long.parseLong(searchCriteria);
            list = productRepository.getProductProjectionsWhenSearchCriteriaIsNumeric(searchCriteria, AutorisationUtils.getCurrentUserMid());
        } catch (NumberFormatException e) {
            list = productRepository.getProductOnNomenclature(searchCriteria, AutorisationUtils.getCurrentUserMid());
        }
        model.addAttribute("flatData", wrap(list.stream().map(str -> getOption(str, pattern)).toList()));
        return "fragments/elements/flatData";
    }

    private String wrap(List<String> listCostings) {
        return (!listCostings.isEmpty()) ?
                "<ul style=\"max-height: 180px; overflow: auto;\">" + String.join("", listCostings) + "</ul>"
                : "<ul style=\"max-height: 180px; overflow: auto;\">No record found</ul>";
    }

    private String getOption(ProductProjection proj, Pattern pattern) {
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