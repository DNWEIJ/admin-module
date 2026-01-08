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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitAnalyseController {
    private final VisitRepository visitRepository;
    private final AnalyseItemRepository analyseItemRepository;
    private final AnalyseRepository analyseRepository;
    private final LineItemService lineItemService;

    // selection of the dropdown
    @GetMapping("/visit/{visitId}/analyse/{analyseDescriptionId}")
    String getAnalyses(@PathVariable Long visitId, @PathVariable Long analyseDescriptionId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        List<Analyse> list = analyseRepository.findByMemberIdAndAnalyseDescription_Id(AutorisationUtils.getCurrentUserMid(), analyseDescriptionId);
        model
                .addAttribute("analyseItems", list.stream().map(a ->
                                createAnalyseItem(a, lineItemService.createConsultAnalyseLineItem(a.getCosting().getId(), a.getQuantity(), visit.getPet()), visit)
                        ).toList()
                );

        return "consult-module/visit/analyselist";
    }


    private List<AnalyseItem> createAnalyseItem(Analyse analyse, List<LineItem> lineItems, Visit visit) {
        return lineItems.stream().map(lineItem ->
                (AnalyseItem) AnalyseItem.builder()
                        .costingId(analyse.getCosting().getId())
                        .analyseId(analyse.getId())
                        .nomenclature(analyse.getCosting().getNomenclature())
                        .appointmentId(visit.getAppointment().getId())
                        .comment("")
                        .ownerIndicator(YesNoEnum.No)
                        .vetIndicator(YesNoEnum.No)
                        .quantity(lineItem.getQuantity())
                        .exclPrice(lineItem.getSalesPriceExTax())
                        .inclPrice(lineItem.getTotalIncTax())
                        .taxedType(lineItem.getTaxedTypeEnum())
                        .petId(visit.getPet().getId())
                        .build()
        ).toList();
    }
}