package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Analyse;
import dwe.holding.salesconsult.consult.model.AnalyseItem;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AnalyseItemRepository;
import dwe.holding.salesconsult.consult.repository.AnalyseRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitAnalyseController {
    private final VisitRepository visitRepository;
    private final AnalyseItemRepository analyseItemRepository;
    private final AnalyseRepository analyseRepository;
    private final LineItemService lineItemService;

    // Changed dropdown -> produce list of analyses
    @GetMapping("/visit/{visitId}/analyse/{analyseDescriptionId}")
    String getAnalysesHtmx(@PathVariable Long visitId, @PathVariable Long analyseDescriptionId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> definedAnalyseList = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseDescriptionId);
        List<AnalyseItem> result = definedAnalyseList.stream().flatMap(a ->
                createAnalyseItem(a, lineItemService.createConsultAnalyseLineItem(a.getCosting().getId(), a.getQuantity(), visit.getPet()), visit)
                        .stream()).toList();
        model
                .addAttribute("visit", visit)
                .addAttribute("analyseItems", result)
                .addAttribute("isAnalyseItemsFromDb", false);

        return "consult-module/visit/analyselist";
    }


    @PostMapping("/visit/{visitId}/analyse/copy")
    String saveAnalysesHtmx(@PathVariable Long visitId, @PathVariable Long analyseDescriptionId, Model model) {

    return "";
    }

    private List<AnalyseItem> createAnalyseItem(Analyse analyse, List<LineItem> lineItems, Visit visit) {
        AtomicLong counter = new AtomicLong(1);
        return lineItems.stream().map(lineItem ->
                        (AnalyseItem) AnalyseItem.builder()
                                .id(counter.getAndIncrement())
                                .appointmentId(visit.getAppointment().getId())
                                .petId(visit.getPet().getId())
                                .costingId(analyse.getCosting().getId())
                                .categoryId(lineItem.getCategoryId())
                                .analyseId(analyse.getId())
                                .comment("")
                                .ownerIndicator(YesNoEnum.No)
                                .vetIndicator(YesNoEnum.No)

                                .nomenclature(analyse.getCosting().getNomenclature())
                                .salesPriceExTax(lineItem.getSalesPriceExTax())
                                .processingFeeExTax(lineItem.getProcessingFeeExTax())
                                .quantity(lineItem.getQuantity())
                                .taxPortionOfProduct(lineItem.getTaxPortionOfProduct())
                                .taxPortionOfProcessingFeeService(lineItem.getTaxPortionOfProcessingFeeService())
                                .taxedTypeEnum(lineItem.getTaxedTypeEnum())
                                .taxGoodPercentage(lineItem.getTaxGoodPercentage())
                                .taxServicePercentage(lineItem.getTaxServicePercentage())
                                .totalIncTax(lineItem.getTotalIncTax())
                                .build()
                // inital move, so all is alrready calculated
                //  analyseItem.calculateTotal(null);
        ).toList();
    }
}