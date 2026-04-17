package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.PrescriptionLabel;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.PrescriptionLabelRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/sales")
public class ReportLabelController {
    private final LineItemService lineItemService;
    private final VisitRepository visitRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final PrescriptionLabelRepository prescriptionLabelRepository;

    @GetMapping("/consult/visit/customer/{customerId}/visit/{visitId}/lineitem/{lineItemId}/label")
    String showLabelModal(@PathVariable Long customerId, @PathVariable Long visitId, @PathVariable Long lineItemId, ModelMap model) {

        Visit visit = visitRepository.findById(visitId).orElseThrow();
        if (!visit.getPet().getCustomer().getId().equals(customerId)) {
            throw new NoSuchElementException("No value present");
        }
        LineItem lineItem = lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()).stream().filter(line -> line.getId().equals(lineItemId)).findFirst().orElseThrow();
        Product product = productRepository.findById(lineItem.getProductId()).orElseThrow();

        Optional optionalPrescriptionLabel = prescriptionLabelRepository.getPrescriptionLabelByLineItemId(lineItemId);
        PrescriptionLabel prescriptionLabel = null;
        if (optionalPrescriptionLabel.isPresent()) {
            prescriptionLabel = (PrescriptionLabel) optionalPrescriptionLabel.get();
        } else {
            prescriptionLabel = PrescriptionLabel.builder()
                    .ownerName(visit.getPet().getCustomer().getCustomerName())
                    .petName(visit.getPet().getName())
                    .expirationDate(LocalDate.now())
                    .drugDosage(product.getNomenclature())
                    .usageDescription(product.getPrescriptionLabel())
                    .staffMember(AutorisationUtils.getCurrentUserName())
                    .build();
        }
        model
                .addAttribute("rxlabel", prescriptionLabel)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("visit", visit)
                .addAttribute("lineItem", lineItem)
                .addAttribute("productSearchUrl", "/visit/customer/" + customerId + "/visit/" + visit.getId())
        ;
        return "sales-module/medicallabel/labelmodal";
    }

    @PostMapping("/customer/{customerId}/visit/{visitId}/lineitem/{lineItemId}/label")
    String printOrSaveRxLabel(@PathVariable Long customerId, @PathVariable Long visitId, @PathVariable Long lineItemId,
                              PrescriptionLabel prescriptionLabelForm, String submitButton, HttpServletResponse response, Model model) {

        Visit visit = visitRepository.findById(visitId).orElseThrow();
        if (!visit.getPet().getCustomer().getId().equals(customerId)) {
            throw new NoSuchElementException("No value present");
        }
        LineItem lineItem = lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()).stream().filter(line -> line.getId().equals(lineItemId)).findFirst().orElseThrow();

        if (submitButton.equals("_print")) {
            model
                    .addAttribute("rxlabel", prescriptionLabelForm)
                    .addAttribute("clinic", AutorisationUtils.getCurrentLocalMember().getLocalMemberName())
                    .addAttribute("phone", AutorisationUtils.getCurrentLocalMember().getPhone1())
                    .addAttribute("today", LocalDate.now().toString())
            ;
            return "reporting-module/print/prescriptionLabel";
        }
        if (prescriptionLabelForm.getId() == null) {
            prescriptionLabelRepository.save(
                    PrescriptionLabel.builder()
                            .staffMember(prescriptionLabelForm.getStaffMember())
                            .ownerName(prescriptionLabelForm.getOwnerName())
                            .petName(prescriptionLabelForm.getPetName())
                            .expirationDate(prescriptionLabelForm.getExpirationDate())
                            .drugDosage(prescriptionLabelForm.getDrugDosage())
                            .usageDescription(prescriptionLabelForm.getUsageDescription())
                            .lineItemId(lineItem.getId())
                            .appointmentId(visit.getAppointment().getId())
                            .build());
        } else {
            PrescriptionLabel prescriptionLabel = prescriptionLabelRepository.findById(prescriptionLabelForm.getId()).orElseThrow();
            prescriptionLabel.setStaffMember(prescriptionLabelForm.getStaffMember());
            prescriptionLabel.setOwnerName(prescriptionLabelForm.getOwnerName());
            prescriptionLabel.setPetName(prescriptionLabelForm.getPetName());
            prescriptionLabel.setExpirationDate(prescriptionLabelForm.getExpirationDate());
            prescriptionLabel.setDrugDosage(prescriptionLabelForm.getDrugDosage());
            prescriptionLabel.setUsageDescription(prescriptionLabelForm.getUsageDescription());
            prescriptionLabelRepository.save(prescriptionLabel);
        }
        response.setHeader("HX-Trigger", "closeModal");
        return "fragments/elements/empty";
    }
}
