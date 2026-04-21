package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Analyse;
import dwe.holding.salesconsult.consult.model.AnalyseItem;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AnalyseItemRepository;
import dwe.holding.salesconsult.consult.repository.AnalyseRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.salesconsult.sales.model.CostCalc;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateLineItemsInModel;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
@Slf4j
public class HtmxVisitAnalyseController {
    private final VisitRepository visitRepository;
    private final AnalyseItemRepository analyseItemRepository;
    private final AnalyseRepository analyseRepository;
    private final LineItemService lineItemService;
    private final ProductService productService;
    private final MessageSource messageSource;

    // Changed dropdown -> produce list of analyses
    @GetMapping("/visit/{visitId}/analyse/{analyseDescriptionId}")
    String getAnalysesHtmx(@PathVariable Long visitId, @PathVariable Long analyseDescriptionId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseDescriptionId);

        List<LineItem> result = definedAnalyseList.stream().flatMap(a ->
                lineItemService.createConsultAnalyseLineItem(a.getProduct().getId(), a.getQuantity(), visit.getPet(), a.getId()).stream()).toList();
        model
                .addAttribute("visit", visit)
                .addAttribute("analyseItems", result)
                .addAttribute("analyseTotalAmount", result.stream().map(CostCalc::getTotalIncTax).reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .addAttribute("analyseTotalVatAmount", result.stream().map(c -> c.getTaxPortionOfProduct().add(c.getTaxPortionOfProcessingFeeService())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .addAttribute("isAnalyseItemsFromDb", false);

        return "consult-module/visit/analyselist";
    }


    @PostMapping("/visit/{visitId}/analyse/copy")
    String saveAnalyseAndCreateLineItemHtmx(@PathVariable Long visitId, AnalyseForm analyseForm, Model model) {
        analyseForm = fixForm(analyseForm);

        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseForm.analyseDropDown());

        Map<Long, AnalyseItemForm> userMap = analyseForm.analyseItems.stream().collect(Collectors.toMap(element -> element.productId, element -> element));
        List<AnalyseItem> analyseItemToBeSaved = new ArrayList<>();

        List<LineItem> lineItemsToBeSaved = definedAnalyseList.stream().flatMap(formAnalyse ->
                lineItemService.createConsultAnalyseLineItem(formAnalyse.getProduct().getId(),
                                (userMap.get(formAnalyse.getProduct().getId()).quantity() == null) ? BigDecimal.ZERO : userMap.get(formAnalyse.getProduct().getId()).quantity(), // get Form quantity
                                visit.getPet(), null).stream()
                        // find records that do have vet and owner YES (being the true on the checkbox)
                        .filter(lineItem -> {
                                    AnalyseItemForm rec = userMap.get(lineItem.getProductId());
                                    if (rec == null) {
                                        throw new RuntimeException("AnalyseForm doesn't contain productId");
                                    }

                                    mapFormToAnalyseItem(analyseItemToBeSaved, lineItem, rec, visit);
                                    return (rec.ownerIndicator && rec.vetIndicator);
                                }
                        )
        ).toList();

        updateLineItemsInModel(model, lineItemService.saveAnalyseAndLineItem(analyseItemToBeSaved, lineItemsToBeSaved, visit));
        model
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("analyseItems", analyseItemRepository.findByMemberIdAndAppointmentIdAndPetId(AutorisationUtils.getCurrentUserMid(), visit.getAppointment().getId(), visit.getPet().getId()))
                .addAttribute("isAnalyseItemsFromDb", true)
                .addAttribute("customerId", visit.getPet().getCustomer().getId())
                .addAttribute("petId", visit.getPet().getId())
                .addAttribute("productSearchUrl", VisitController.VISIT_URL.replace("{customerId}", visit.getPet().getCustomer().getId().toString()).replace("{visitId}", visit.getId().toString()))
                .addAttribute("categoryNames", productService.getAllCategoriesInclDeleted())
                .addAttribute("salesType", SalesType.VISIT);
        return "/consult-module/fragments/htmx/replaceanalyseandlineitems";
    }

    @PostMapping("/visit/{visitId}/analyse/update")
    String saveAnalyseCommentsHtmx(@PathVariable Long visitId, AnalyseForm analyseForm, Model model, HttpServletResponse response, Locale locale) {

        Map<Long, AnalyseItemForm> map = analyseForm.analyseItems().stream().collect(Collectors.toMap(AnalyseItemForm::id, item -> item));
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<AnalyseItem> analyseItems = analyseItemRepository.findByMemberIdAndAppointmentIdAndPetId(AutorisationUtils.getCurrentUserMid(), visit.getAppointment().getId(), visit.getPet().getId());
        analyseItems.forEach(element -> {
            element.setComment((map.get(element.getId()).comment() == null) ? "" : map.get(element.getId()).comment());
        });
        List<AnalyseItem> elementsSaved = analyseItemRepository.saveAll(analyseItems);
        model
                .addAttribute("visit", visit)
                .addAttribute("analyseItems", elementsSaved)
                .addAttribute("isAnalyseItemsFromDb", true);
        response.setHeader("HX-Trigger", "{\"messageDisplay\":{\"messageText\":\"" + messageSource.getMessage("label.saved", null, locale) + "\"}}");
        return "consult-module/visit/analyselist";
    }


    private void mapFormToAnalyseItem(List<AnalyseItem> list, LineItem lineItem, AnalyseItemForm form, Visit visit) {
        AnalyseItem item = new AnalyseItem();
        item.setVetIndicator(form.vetIndicator() == null || !form.vetIndicator() ? YesNoEnum.No : YesNoEnum.Yes);
        item.setOwnerIndicator(form.ownerIndicator() == null || !form.ownerIndicator() ? YesNoEnum.No : YesNoEnum.Yes);
        item.setQuantity(form.quantity() == null ? BigDecimal.ZERO : form.quantity());
        item.setAppointmentId(visit.getAppointment().getId());
        item.setPetId(visit.getPet().getId());
        item.setNomenclature(lineItem.getNomenclature());
        item.setProductId(lineItem.getProductId());
        item.setAnalyseId(form.id());
        item.setId(null);
        list.add(item);
    }

    @With
    record AnalyseItemForm(Long id, BigDecimal quantity, Boolean ownerIndicator, Boolean vetIndicator, Long productId, String comment) {
    }

    record AnalyseForm(Long analyseDropDown, List<AnalyseItemForm> analyseItems) {
    }

    AnalyseForm fixForm(AnalyseForm analyseForm) {
        return new AnalyseForm(
                analyseForm.analyseDropDown(),
                analyseForm.analyseItems().stream()
                        .map(item -> item
                                .withOwnerIndicator(Boolean.TRUE.equals(item.ownerIndicator()))
                                .withVetIndicator(Boolean.TRUE.equals(item.vetIndicator()))
                                .withQuantity(
                                        item.quantity() == null ? BigDecimal.ZERO : item.quantity()
                                )
                        )
                        .toList()
        );
    }
}