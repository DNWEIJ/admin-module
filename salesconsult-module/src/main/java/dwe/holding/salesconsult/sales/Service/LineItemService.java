package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.Reminder;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.AnalyseItem;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AnalyseItemRepository;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
import dwe.holding.supplyinventory.model.projection.CostingPriceProjection;
import dwe.holding.supplyinventory.repository.ReminderRepository;
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
    private final ReminderRepository reminderRepository;
    private final CustomerService customerService;
    private final AnalyseItemRepository analyseItemRepository;


    public List<LineItem> createPricing(@NotNull Long costingId, @NotNull BigDecimal quantity) {
        return createLineItemsFromCosting(Appointment.builder().id(0L).build(), costingId, quantity, Pet.builder().id(0L).build());
    }

    public List<LineItem> createEstimateLineItem(@NotNull Long costingId, @NotNull BigDecimal quantity, @NotNull Pet pet) {
        return createLineItemsFromCosting(Appointment.builder().id(0L).build(), costingId, quantity, pet);
    }

    public List<LineItem> createConsultAnalyseLineItem(@NotNull Long costingId, @NotNull BigDecimal quantity, @NotNull Pet pet, Long analyseId) {
        List<LineItem> lineItems = createLineItemsFromCosting(Appointment.builder().id(0L).build(), costingId, quantity, pet);
        lineItems.forEach(lineItem -> lineItem.setId(analyseId));
        return lineItems;
    }


    @Transactional
    public void createOtcLineItem(Appointment app, Long petId, Long costingId, BigDecimal quantity, String batchNumber, String spillageName) {

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

        final List<LineItem> toBeSavedLineItems = createLineItemsFromCosting(app, costingId, quantity, batchNumber, spillageName, foundVisit.getPet());

        List<LineItem> savedLineItems = lineItemRepository.saveAll(toBeSavedLineItems);
        app.getLineItems().addAll(savedLineItems);
        appointmentRepository.save(app);
    }

    public void delete(@NotNull Long lineItemId) {
        lineItemRepository.deleteById(lineItemId);
    }

    public List<LineItem> getLineItemsForPet(Long petId, Long appointmentId) {
        return lineItemRepository.findByPet_IdAndAppointment_IdAndMemberId(petId, appointmentId, AutorisationUtils.getCurrentUserMid());
    }

    private List<LineItem> createLineItemsFromCosting(Appointment appointment, Long costingId, BigDecimal quantity, Pet pet) {
        return createLineItemsFromCosting(appointment, costingId, quantity, null, null, pet);
    }

    private List<LineItem> createLineItemsFromCosting(Appointment appointment, Long costingId, BigDecimal quantity, String batchNumber, String spillageName, Pet pet) {
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
            LineItem newLineItem = createLineItem(appointment, cpp, quantity, pet, taxes, costingGroupList, costingId);

            if (appointment.getId() == 0L) { // we are in pricing or estimate or analyse
                toBeSavedLineItems.add(newLineItem);
            } else {    // we are in OTC or Consult
                LineItem savedLineItem = lineItemRepository.save(newLineItem);

                if (cpp.hasSpillage().equals(YesNoEnum.Yes)) {
                    costingService.createOrUpdateSpillage(cpp.id(), spillageName, savedLineItem.getId());
                }
                if (cpp.hasBatchNr().equals(YesNoEnum.Yes)) {
                    costingService.createBatchNumberIfNotExisting(cpp.id(), batchNumber);
                }
                // do extra stuff we need to do on costings....
                doExtraStuff(cpp, pet, appointment);
                toBeSavedLineItems.add(savedLineItem);
            }
        });
        return toBeSavedLineItems;
    }


    /**************************************************************************************************************/
    /*      THIS IS THE HEART OF ALL LINEITEM CALCULATIONS, FOR ALL TYPES OTC / VISIT / ESTIMATE / ANALYSE        */
    /*                                                                                                            */
    /*                               BE CAREFULLY CHANGING STUFF HERE                                             */

    /**************************************************************************************************************/
    private LineItem createLineItem(Appointment appointment, CostingPriceProjection cpp, BigDecimal quantity, Pet pet,
                                    LocalMemberTax taxes, Map<Long, BigDecimal> costingGroupList, Long costingId) {
        LineItem newLineItem = LineItem.builder().appointment(appointment)
                .pet(pet)
                .processingFeeExTax(cpp.processingFeeExTax())
                .taxedTypeEnum(cpp.taxed())
                .salesPriceExTax(cpp.salesPriceExTax())
                .categoryId(cpp.lookupCostingCategory().getId())
                .costingId(cpp.id())
                .nomenclature(cpp.nomenclature())
                .taxGoodPercentage(taxes.getTaxHigh())
                .taxServicePercentage(taxes.getTaxLow())
                // calculations
                .quantity(
                        getQuantity(quantity, costingGroupList.get(costingId))
                )
                .hasPrintLabel(cpp.prescriptionLabel() != null && !cpp.prescriptionLabel().isEmpty())
                .build();

        newLineItem.setTotalIncTax(newLineItem.calculateTotal(cpp.reductionPercentage()));
        newLineItem.setTaxPortionOfProcessingFeeService(newLineItem.calculateProcessingFeeServiceTax());
        newLineItem.setTaxPortionOfProduct(newLineItem.calculateCostTaxPortion());
        return newLineItem;
    }

    private BigDecimal getQuantity(BigDecimal quantity, BigDecimal groupQuantity) {
        if (groupQuantity == null) {
            return quantity;
        }
        return quantity.multiply(groupQuantity);
    }

    private void doExtraStuff(CostingPriceProjection cpp, Pet pet, Appointment appointment) {
        //update balance
//        getUserSession().setCustomerBalance(getUserSession().getCustomerBalance() - l.getTotal());

        // update visit total amount

        // update reminder status
        if (YesNoEnum.Yes.equals(cpp.autoReminder())) {
            // clean up existing reminders
            reminderRepository.deleteAllByPet_IdAndReminderTextContainingIgnoreCase(pet.getId(), cpp.rRemovePendingRemindersContaining());
            reminderRepository.save(
                    Reminder.builder()
                            .reminderText(cpp.reminderNomenclature())
                            .pet(pet)
                            .dueDate(LocalDate.from(appointment.getVisitDateTime().plusWeeks(cpp.intervalInWeeks())))
                            .originatingAppointmentId(appointment.getId())
                            .build()
            );
        }
        //update patient Deceased
        if (YesNoEnum.Yes.equals(cpp.deceasedPetPrompt())) {
            customerService.updatePetDeceased(pet.getId());
        }
    }

    @Transactional
    public Set<LineItem> saveAnalyseAndLineItem(List<AnalyseItem> analyseItemList, List<LineItem> lineItemsList, Visit visit) {
        analyseItemRepository.saveAll(analyseItemList);
        Appointment app = appointmentRepository.findById(visit.getAppointment().getId()).orElseThrow();
        lineItemsList.forEach(li -> li.setAppointment(app));
        app.getLineItems().addAll(lineItemsList);
        lineItemRepository.saveAll(lineItemsList);
        Appointment savedApp = appointmentRepository.save(app);
        savedApp.getLineItems().size();
        return savedApp.getLineItems();
    }
}