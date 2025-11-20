package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
import dwe.holding.supplyinventory.model.projection.CostingPriceProjection;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@AllArgsConstructor
public class LineItemService {

    private final LineItemRepository lineItemRepository;
    private final CostingService costingService;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public List<LineItem> createOTC(Appointment app, Long petId, Long costingId, BigDecimal amount, String batchNumber, String spillageName) {

        // validate if we are allowed to add a line item
        if (app.getCancelled().equals(YesNoEnum.Yes) || app.getCompleted().equals(YesNoEnum.Yes)) {
            if (AutorisationUtils.hasStatus(PersonnelStatusEnum.Vet)) {
                Set<Visit> set = app.getVisits();
                Iterator<Visit> iterator = set.iterator();
                while (iterator.hasNext()) {
                    Visit visit = iterator.next();
                    if (VisitStatusEnum.isClosed(visit.getStatus())) {
                        throw new SecurityException("Cannot add line items; Appointment or Visit status doesn't allow it");
                    }
                }
            }
        }

        Visit foundVisit = app.getVisits().stream().filter(visit -> visit.getPet().getId().equals(petId)).findFirst().get();

        // do we need to add all grouping products?
        List<CostingPriceProjection> priceIncludingPromotions = costingService.getCorrectedPriceAndGroupingForCostingId(costingId);

        // we need the quantity of the grouping to calculate
        Map<Long, BigDecimal> costingGroupList;
        if (priceIncludingPromotions.size() > 1) {
            costingGroupList = costingService.getGroupingsQuantity(costingId);
        } else {
            costingGroupList = Map.of();
        }

        // create the lineitems on the invoice per costing
        List<LineItem> toBeSavedLineItems = new ArrayList<>();
        priceIncludingPromotions.forEach(cpp -> {
            // create the lineitem to save; fill it with the generic and reference stuff
            LineItem newLineItem = LineItem.builder().appointmentId(
                            app.getId())
                    .petId(foundVisit.getPet().getId())
                    .localMemberId(90L) // TODO: Autorisaton
                    .processingFee(cpp.processingFee())
                    .taxForSellExTaxPrice(cpp.taxed())
                    .sellExTaxPrice(cpp.sellExTaxPrice())
                    .categoryId(cpp.lookupCostingCategory().getId())
                    .nomenclature(cpp.nomenclature())
                    .taxGoodPercentage(new BigDecimal("21.00")) // TODO member
                    .taxServicePercentage(new BigDecimal("9.00")) // TODO member
                    // calculations
                    .quantity(
                            getQuantity(amount, costingGroupList.get(costingId))
                    )
                    .hasPrintLabel(cpp.prescriptionLabel() != null && !cpp.prescriptionLabel().isEmpty())
                    .build();

            newLineItem.setTotal(newLineItem.calculateTotal(cpp.reductionPercentage()));
            newLineItem.setTaxPortionOfProcessingFeeService(newLineItem.calculateProcessingFeeServiceTax());
            newLineItem.setTaxPortionOfSell(newLineItem.calculateCostTaxPortion());
            LineItem savedLineItem = lineItemRepository.save(newLineItem);

            if (cpp.hasSpillage().equals(YesNoEnum.Yes)) {
                costingService.createOrUpdateSpillage(cpp.id(), spillageName, savedLineItem.getId());
            }
            if (cpp.hasBatchNr().equals(YesNoEnum.Yes)) {
                costingService.createBatchNumberIfNotExisting(cpp.id(), batchNumber);
            }
            toBeSavedLineItems.add(savedLineItem);
        });
        List<LineItem> savedLineItems = lineItemRepository.saveAll(toBeSavedLineItems);
        app.getLineItems().addAll(savedLineItems);
        appointmentRepository.save(app);
        return savedLineItems;
    }

    private BigDecimal getQuantity(BigDecimal amount, BigDecimal groupQuantity) {
        if (groupQuantity == null) {
            return amount;
        }
        return amount.multiply(groupQuantity);
    }
}