package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.repository.*;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.frontend.PresentationElement;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

public class ModelHelper {

    public static Model updateLineItemsInModel(Model model, List<LineItem> lineItems) {
        if (!lineItems.isEmpty()) {
            model.addAttribute("totalAmount", lineItems.stream().map(LineItem::getTotal).reduce(BigDecimal::add).get());
            model.addAttribute("totalVatAmount", lineItems.stream().map(LineItem::getTotal).reduce(BigDecimal::add).get());
        }
        model.addAttribute("allLineItems", lineItems);
        return model;
    }

    public static Model updateReasonsInModel(Model model, LookupPurposeRepository lookupPurposeRepository) {
        model.addAttribute("reasons", lookupPurposeRepository.getByMemberIdOrderByDefinedPurpose(AutorisationUtils.getCurrentUserMid())
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getDefinedPurpose(), true)).toList());
        return model;
    }

    public static Model updateCustomerAndPetsInModel(Model model, CustomerService.Customer customer) {
        model
                .addAttribute("customer", customer)
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).toList());
        return model;
    }

    public static Model updateRoomsInModel(Model model, LookupRoomRepository lookupRoomRepository) {
        model.addAttribute("rooms", lookupRoomRepository.findByLocalMemberIdAndMemberId(AutorisationUtils.getCurrentUserMlid(), AutorisationUtils.getCurrentUserMid())
                .stream().map(rec -> new PresentationElement(rec.getRoom(), rec.getRoom())).toList()
        );
        return model;

    }

    public static Model updateLocationsInModel(Model model, LookupLocationRepository lookupLocationRepository) {
        model.addAttribute("locations", lookupLocationRepository.findAll()
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getNomenclature())).toList()
        );
        return model;
    }

    public static Model updateDiagnosesInModel(Model model, LookupDiagnosesRepository lookupDiagnosesRepository, List<Long> memberIds) {
        model.addAttribute("diagnoses", lookupDiagnosesRepository.findByMemberIdIn(memberIds)
                .stream().map(rec -> new PresentationElement(rec.getId(), rec.getNomenclature())).toList()
        );
        return model;
    }

    public static Model updatePetDiagnosesInModel(Model model, DiagnoseRepository diagnoseRepository, Long memberId, Long petId, Long appointmentId) {
        model.addAttribute("petDiagnoses", diagnoseRepository.findByMemberIdAndPetIdAndAppointmentId(memberId, petId, appointmentId));
        return model;
    }
}