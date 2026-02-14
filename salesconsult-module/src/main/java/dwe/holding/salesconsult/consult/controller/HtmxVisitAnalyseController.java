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
import dwe.holding.supplyinventory.expose.CostingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateLineItemsInModel;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitAnalyseController {
    private final VisitRepository visitRepository;
    private final AnalyseItemRepository analyseItemRepository;
    private final AnalyseRepository analyseRepository;
    private final LineItemService lineItemService;
    private final CostingService costingService;

    // Changed dropdown -> produce list of analyses
    @GetMapping("/visit/{visitId}/analyse/{analyseDescriptionId}")
    String getAnalysesHtmx(@PathVariable Long visitId, @PathVariable Long analyseDescriptionId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseDescriptionId);

        List<LineItem> result = definedAnalyseList.stream().flatMap(a ->
                lineItemService.createConsultAnalyseLineItem(a.getCosting().getId(), a.getQuantity(), visit.getPet(), a.getId()).stream()).toList();
        model
                .addAttribute("visit", visit)
                .addAttribute("analyseItems", result)
                .addAttribute("analyseTotalAmount", result.stream().map(CostCalc::getTotalIncTax).reduce(BigDecimal::add).get())
                .addAttribute("analyseTotalVatAmount", result.stream().map(c -> c.getTaxPortionOfProduct().add(c.getTaxPortionOfProcessingFeeService())).reduce(BigDecimal::add).get())
                .addAttribute("isAnalyseItemsFromDb", false);

        return "consult-module/visit/analyselist";
    }

    // id = analyseId added in the lineItem during creation of the lineItem
    record AnalyseItemForm(Long id, BigDecimal quantity, Boolean ownerIndicator, Boolean vetIndicator, Long costingId) {
    }

    record AnalyseForm(Long analyseDropDown, List<AnalyseItemForm> analyseItems) {
    }

    @PostMapping("/visit/{visitId}/analyse/copy")
    String saveAnalyseAndCreateLineItemHtmx(@PathVariable Long visitId, AnalyseForm analyseForm, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseForm.analyseDropDown());

        Map<Long, AnalyseItemForm> userMap = analyseForm.analyseItems.stream().collect(Collectors.toMap(element -> element.costingId, element -> element));
        List<AnalyseItem> analyseItemToBeSaved = new ArrayList<>();
        List<LineItem> lineItemsToBeSaved = definedAnalyseList.stream().flatMap(a ->
                lineItemService.createConsultAnalyseLineItem(a.getCosting().getId(), a.getQuantity(), visit.getPet(), null).stream()
                        // find records that do not have a vet or owner NO (being the true on the checkbox)
                        .filter(lineItem -> {
                                    AnalyseItemForm rec = userMap.get(lineItem.getCostingId());
                                    if (rec == null) {
                                        throw new RuntimeException("AnalyseForm doesn't contain costingId");
                                    }

                                    mapFormToAnalyseItem(analyseItemToBeSaved, lineItem, rec, visit);
                                    return (rec.ownerIndicator == null || rec.ownerIndicator.booleanValue() == false) && (rec.vetIndicator == null || rec.vetIndicator.booleanValue() == false);
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
                .addAttribute("url", VisitController.VISIT_URL.replace("{customerId}", visit.getPet().getCustomer().getId().toString()).replace("{visitId}", visit.getId().toString()))
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("salesType", SalesType.VISIT);
        return "/consult-module/fragments/htmx/replaceanalyseandlineitems";
    }

    @PostMapping("/visit/{visitId}/analyse/update")
    String saveAnalyseCommentsHtmx(@PathVariable Long visitId, AnalyseForm analyseForm, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseForm.analyseDropDown());
        return "";
    }


        private void mapFormToAnalyseItem(List<AnalyseItem> list, LineItem lineItem, AnalyseItemForm form, Visit visit) {
        AnalyseItem item = new AnalyseItem();
        item.setVetIndicator(form.vetIndicator() == null || form.vetIndicator().booleanValue() == false ? YesNoEnum.No : YesNoEnum.Yes);
        item.setOwnerIndicator(form.ownerIndicator() == null || form.ownerIndicator().booleanValue() == false ? YesNoEnum.No : YesNoEnum.Yes);
        item.setQuantity(form.quantity() == null ? BigDecimal.ZERO : form.quantity());
        item.setAppointmentId(visit.getAppointment().getId());
        item.setPetId(visit.getPet().getId());
        item.setNomenclature(lineItem.getNomenclature());
        item.setCostingId(lineItem.getCostingId());
        item.setAnalyseId(form.id());
        item.setId(null);
        list.add(item);
    }
}