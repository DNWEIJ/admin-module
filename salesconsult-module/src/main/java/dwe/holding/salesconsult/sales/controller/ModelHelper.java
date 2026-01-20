package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.*;
import dwe.holding.salesconsult.sales.model.CostCalc;
import dwe.holding.shared.model.frontend.PresentationElement;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ModelHelper {

    public static Model updateLineItemsInModel(Model model, Collection<? extends CostCalc> lineItems) {
        if (!lineItems.isEmpty()) {
            model.addAttribute("totalAmount", lineItems.stream().map(CostCalc::getTotalIncTax).reduce(BigDecimal::add).get());
            model.addAttribute("totalVatAmount", lineItems.stream().map(c -> c.getTaxPortionOfProduct().add(c.getTaxPortionOfProcessingFeeService())).reduce(BigDecimal::add).get());
        }
        model.addAttribute("allLineItems", lineItems);
        return model;
    }

    public static Model updateReasonsInModel(Model model, LookupPurposeRepository lookupPurposeRepository) {
        model.addAttribute("reasons", lookupPurposeRepository.getByMemberIdOrderByDefinedPurpose(AutorisationUtils.getCurrentUserMid())
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getDefinedPurpose(), rec.getTimeInMinutes().toString())).toList());
        return model;
    }

    public static Model updateCustomerAndPetsInModel(Model model, CustomerService.Customer customer) {
        model
                .addAttribute("customer", customer)
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).sorted(Comparator.comparing(CustomerService.Pet::name)).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).toList());
        return model;
    }

    public static Model updateRoomsInModel(Model model, LookupRoomRepository lookupRoomRepository) {
        model.addAttribute("rooms", lookupRoomRepository.findByLocalMemberIdAndMemberId(AutorisationUtils.getCurrentUserMlid(), AutorisationUtils.getCurrentUserMid())
                .stream().map(rec -> new PresentationElement(rec.getRoom(), rec.getRoom())).sorted(Comparator.comparing(PresentationElement::getName)).toList()
        );
        return model;

    }

    public static Model updateLocationsInModel(Model model, LookupLocationRepository lookupLocationRepository) {
        model.addAttribute("locations", lookupLocationRepository.findAll()
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getNomenclature())).sorted(Comparator.comparing(PresentationElement::getName)).toList()
        );
        return model;
    }

    public static Model updateDiagnosesInModel(Model model, LookupDiagnosesRepository lookupDiagnosesRepository, List<Long> memberIds) {
        model.addAttribute("diagnoses", lookupDiagnosesRepository.findByMemberIdIn(memberIds)
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getNomenclature())).sorted(Comparator.comparing(PresentationElement::getName)).toList()
        );
        return model;
    }

    public static Model updatePetDiagnosesInModel(Model model, DiagnoseRepository diagnoseRepository, Long memberId, Long petId, Long appointmentId) {
        model.addAttribute("petDiagnoses", diagnoseRepository.findByMemberIdAndPetIdAndAppointmentId(memberId, petId, appointmentId));
        return model;
    }

    public static Model updateVisitStatusInModel(Model model, VisitStatusEnum current) {
        record StatusNav(
                VisitStatusEnum previous,
                VisitStatusEnum current,
                Set<VisitStatusEnum> next
        ) {
        }
        model.addAttribute("statusProgress", new StatusNav(current.previous().orElse(null), current, current.nextOptions()));
        return model;
    }

}