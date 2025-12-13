package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.model.LocalMemberTax;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class LineItemService {

    private final LineItemRepository lineItemRepository;
    private final CostingService costingService;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public List<LineItem> createPricing(Long costingId, BigDecimal amount) {
        return getLineItems(Appointment.builder().id(0L).build(), costingId, amount, Pet.builder().id(0L).build());
    }

    @Transactional
    public void createOTC(Appointment app, Long petId, Long costingId, BigDecimal amount, String batchNumber, String spillageName) {

        // validate if we are allowed to add a line item; Only VET can add after closed or canceled
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

        final List<LineItem> toBeSavedLineItems = getLineItems(app, costingId, amount, batchNumber, spillageName, foundVisit.getPet());

        List<LineItem> savedLineItems = lineItemRepository.saveAll(toBeSavedLineItems);
        app.getLineItems().addAll(savedLineItems);
        appointmentRepository.save(app);
    }
    private List<LineItem> getLineItems(Appointment appointment, Long costingId, BigDecimal amount, Pet pet) {
        return getLineItems(appointment,  costingId,  amount, null, null,  pet);
    }

    private List<LineItem> getLineItems(Appointment appointment, Long costingId, BigDecimal amount, String batchNumber, String spillageName, Pet pet) {
        // do we need to add all grouping products?
        List<CostingPriceProjection> priceIncludingPromotions = costingService.getCorrectedPriceAndGroupingForCostingId(costingId);

        // we need the quantity of the grouping to be calculated
        Map<Long, BigDecimal> costingGroupList;
        if (priceIncludingPromotions.size() > 1) {
            costingGroupList = costingService.getGroupingsQuantity(costingId);
        } else {
            costingGroupList = Map.of();
        }

        // create the lineItems on the invoice per costing
        List<LineItem> toBeSavedLineItems = new ArrayList<>();
        priceIncludingPromotions.forEach(cpp -> {
            // create the lineItem to save; fill it with the generic and reference stuff
            LocalMemberTax taxes = AutorisationUtils.getVatPercentages(LocalDate.now());
            LineItem newLineItem = LineItem.builder().appointment(appointment)
                    .pet(pet)
                    .localMemberId(AutorisationUtils.getCurrentUserMlid())
                    .processingFee(cpp.processingFee())
                    .taxedTypeEnum(cpp.taxed())
                    .sellExTaxPrice(cpp.sellExTaxPrice())
                    .categoryId(cpp.lookupCostingCategory().getId())
                    .nomenclature(cpp.nomenclature())
                    .taxGoodPercentage(taxes.getTaxHigh())
                    .taxServicePercentage(taxes.getTaxLow())
                    // calculations
                    .quantity(
                            getQuantity(amount, costingGroupList.get(costingId))
                    )
                    .hasPrintLabel(cpp.prescriptionLabel() != null && !cpp.prescriptionLabel().isEmpty())
                    .build();

            newLineItem.setTotal(newLineItem.calculateTotal(cpp.reductionPercentage()));
            newLineItem.setTaxPortionOfProcessingFeeService(newLineItem.calculateProcessingFeeServiceTax());
            newLineItem.setTaxPortionOfSell(newLineItem.calculateCostTaxPortion());


            if (appointment.getId() == 0L) { // we are in pricing
                toBeSavedLineItems.add(newLineItem);
            } else {    // we are in OTC or Consult
                LineItem savedLineItem = lineItemRepository.save(newLineItem);

                if (cpp.hasSpillage().equals(YesNoEnum.Yes)) {
                    costingService.createOrUpdateSpillage(cpp.id(), spillageName, savedLineItem.getId());
                }
                if (cpp.hasBatchNr().equals(YesNoEnum.Yes)) {
                    costingService.createBatchNumberIfNotExisting(cpp.id(), batchNumber);
                }
                toBeSavedLineItems.add(savedLineItem);
            }
        });
        return toBeSavedLineItems;
    }

    private BigDecimal getQuantity(BigDecimal amount, BigDecimal groupQuantity) {
        if (groupQuantity == null) {
            return amount;
        }
        return amount.multiply(groupQuantity);
    }

    public void delete(@NotNull Long lineItemId) {
        lineItemRepository.deleteById(lineItemId);
    }

    public List<LineItem> getLineItemsForPet(Long petId, Long appointmentId) {
        return lineItemRepository.findByPet_IdAndAppointment_IdAndMemberId(petId, appointmentId, AutorisationUtils.getCurrentUserMid());
    }
}