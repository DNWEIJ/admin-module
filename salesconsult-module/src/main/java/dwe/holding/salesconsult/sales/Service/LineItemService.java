package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.Reminder;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
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
import dwe.holding.supplyinventory.expose.ProductService;
import dwe.holding.supplyinventory.model.projection.ProductPriceProjection;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class LineItemService {

    private final LineItemRepository lineItemRepository;
    private final ProductService productService;
    private final AppointmentRepository appointmentRepository;
    private final ReminderRepository reminderRepository;
    private final CustomerService customerService;
    private final AnalyseItemRepository analyseItemRepository;
    private final FinancialServiceInterface financialService;

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
    public boolean createOtcAndConsultLineItem(Appointment app, Long petId, Long costingId, BigDecimal quantity, String batchNumber, String spillageName) {

        // validate if we are allowed to add a line item; Only VET can add after closed or canceled
        if (app.getCancelled().equals(YesNoEnum.Yes) || app.getCompleted().equals(YesNoEnum.Yes)) {
            if (AutorisationUtils.hasStatus(PersonnelStatusEnum.Vet)) {
                Set<Visit> set = app.getVisits();
                for (Visit visit : set) {
                    if (visit.getPet().getId().equals(petId)) {
                        if (VisitStatusEnum.isClosed(visit.getStatus())) {
                            throw new SecurityException("Cannot add line items; Appointment or Visit status doesn't allow it");
                        }
                    }
                }
            }
        }

        Visit foundVisit = app.getVisits().stream().filter(visit -> visit.getPet().getId().equals(petId)).findFirst().get();
        final List<LineItem> toBeSavedLineItems = createLineItemsFromCosting(app, costingId, quantity, batchNumber, spillageName, foundVisit.getPet());

        List<LineItem> savedLineItems = lineItemRepository.saveAll(toBeSavedLineItems);
        app.getLineItems().addAll(savedLineItems);

        // for OTC, we changed the status to PAYMENT, after the first lineItem
        boolean changedStatus = false;
        if (app.isOTC() && VisitStatusEnum.FINISHED_CONSULT.equals(foundVisit.getStatus())) {
            foundVisit.setStatus(VisitStatusEnum.PAYMENT);
            changedStatus = true;
        }
        appointmentRepository.save(app);
        financialService.updateCustomerBalanceAndVisitTotal(foundVisit.getPet().getCustomer().getId(), foundVisit.getId());
        return changedStatus;
    }


    /** after analyse has been discussed with the customer / vet these are the lineitems we are adding */
    /**
     * we update the balance as well
     */
    @Transactional
    public Set<LineItem> saveAnalyseAndLineItem(List<AnalyseItem> analyseItemList, List<LineItem> lineItemsList, Visit visit) {
        analyseItemRepository.saveAll(analyseItemList);
        Appointment app = appointmentRepository.findById(visit.getAppointment().getId()).orElseThrow();
        lineItemsList.forEach(li -> li.setAppointment(app));
        app.getLineItems().addAll(lineItemsList);
        lineItemRepository.saveAll(lineItemsList);
        Appointment savedApp = appointmentRepository.save(app);
        savedApp.getLineItems().size();
        financialService.updateCustomerBalanceAndVisitTotal(visit.getPet().getCustomer().getId(), visit.getId());
        return savedApp.getLineItems();
    }


    @Transactional
    public Visit deleteLineItemFromVisit(Visit visit, Long lineItemId) {
        Appointment app = visit.getAppointment();
        app.getLineItems().removeIf(lItem -> lItem.getId().equals(lineItemId));
        appointmentRepository.save(app);
        financialService.updateCustomerBalanceAndVisitTotal(visit.getPet().getCustomer().getId(), visit.getId());
        return visit;

    }

    public List<LineItem> getLineItemsForPet(Long petId, Long appointmentId) {
        return lineItemRepository.findByPet_IdAndAppointment_IdAndMemberId(petId, appointmentId, AutorisationUtils.getCurrentUserMid());
    }

    private List<LineItem> createLineItemsFromCosting(Appointment appointment, Long costingId, BigDecimal quantity, Pet pet) {
        return createLineItemsFromCosting(appointment, costingId, quantity, null, null, pet);
    }

    private List<LineItem> createLineItemsFromCosting(Appointment appointment, Long costingId, BigDecimal quantity, String batchNumber, String spillageName, Pet pet) {
        // do we need to add all grouping products?
        List<ProductPriceProjection> priceIncludingPromotions = productService.getCorrectedPriceAndGroupingForCostingId(costingId);

        // we need the quantity of the grouping to be calculated
        Map<Long, BigDecimal> costingGroupList;
        if (priceIncludingPromotions.size() > 1) {
            costingGroupList = productService.getGroupingsQuantity(costingId);
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
                    productService.createOrUpdateSpillage(cpp.id(), spillageName, savedLineItem.getId());
                }
                if (cpp.hasBatchNr().equals(YesNoEnum.Yes)) {
                    productService.createBatchNumberIfNotExisting(cpp.id(), batchNumber);
                }
                // todo change supply amount
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
    private LineItem createLineItem(Appointment appointment, ProductPriceProjection cpp, BigDecimal quantity, Pet pet,
                                    LocalMemberTax taxes, Map<Long, BigDecimal> costingGroupList, Long costingId) {
        LineItem newLineItem = LineItem.builder().appointment(appointment)
                .pet(pet)
                .processingFeeExTax(cpp.processingFeeExTax())
                .taxedTypeEnum(cpp.taxed())
                .salesPriceExTax(cpp.salesPriceExTax())
                .categoryId(cpp.lookupProductCategory().getId())
                .costingId(cpp.id())
                .nomenclature(cpp.nomenclature())
                .taxGoodPercentage(taxes.getTaxLow())
                .taxServicePercentage(taxes.getTaxHigh())
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

    private void doExtraStuff(ProductPriceProjection cpp, Pet pet, Appointment appointment) {
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
        // TODO update inventory

        if (cpp.supplyId() != null) {
            // TODO
            //       calculateUsage(cpp.supplyId(), AutorisationUtils.getCurrentUserMlid(), costing.getSupplies2Idindyqtydeduction() * lineItem.getQuantity());
        }


    }
//
//    public void calculateUsage(Long suppliesId, Long mlid, Double usage) throws ApplicationException {
//        List<Supplies2Local> list = suppliesinventoryDao.getOneSupplies2Local(mlid, suppliesId);
//        Supplies2Local suppliesLocal = new Supplies2Local();
//        if(list != null && list.size() > 0) {
//            suppliesLocal = list.get(0);
//            Supplies2 supplies = this.getSupplies2ById(suppliesLocal.getSupplies2().getId());
//            Double qpk = supplies.getQuantityPerPackage();
//            Double quantity = suppliesLocal.getQuantity();
//            Double individualQuantity = suppliesLocal.getIndividualQuantity();
//            Double totalCount = CommonUtils.add(CommonUtils.mul(qpk, quantity), individualQuantity);
//            if(usage > totalCount) {
//                usage = totalCount;
//                //throw new ApplicationException("SYS-00026");
//            }
//            Double result = CommonUtils.cut(totalCount, usage);
//            quantity = CommonUtils.div(result, qpk, 2);
//            Integer v1 = Integer.valueOf(result.toString().split("\\.")[0]);
//            Integer v2 = (Integer.valueOf(qpk.toString().split("\\.")[0]));
//            Integer v3 = v1 % v2;
//            individualQuantity = Double.valueOf(v3.toString() + ".0");
//            quantity = Double.valueOf(quantity.toString().split("\\.")[0] + ".0");
//            suppliesLocal.setQuantity(quantity);
//            suppliesLocal.setIndividualQuantity(individualQuantity);
//            suppliesinventoryDao.saveOrUpdate(suppliesLocal);
//        }
}