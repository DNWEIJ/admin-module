package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Diagnose;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.*;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
@Slf4j
public class HtmxVisitDiagnoseController {
    private final LookupDiagnosesRepository lookupDiagnosesRepository;
    private final LookupLocationRepository lookupLocationRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final VisitRepository visitRepository;
    private final AppointmentRepository appointmentRepository;

//    @GetMapping("/visit/diagnose")
//    String optionsForDiagnose(@RequestParam boolean ownDiagnoses, Model model) {
//        updateDiagnosesInModel(model, lookupDiagnosesRepository, AutorisationUtils.getCurrentUserMid()
//        );
//        return "/consult-module/fragments/htmx/diagnosefield";
//    }

    @PostMapping("/visit/{visitId}/diagnose")
    String saveDiagnose(@PathVariable Long visitId, Model model, @NotNull Long diagnoseDropdown, @NotNull Long locationDropdown) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        Diagnose diagnose = Diagnose.builder()
                .lookupDiagnose(lookupDiagnosesRepository.findById(diagnoseDropdown).orElseThrow())
                .lookupLocation(lookupLocationRepository.findById(locationDropdown).orElseThrow())
                .appointment(visit.getAppointment())
                .petId(visit.getPet().getId())
                .build();
        diagnoseRepository.save(diagnose);
        visit.getAppointment().getDiagnoses().add(diagnose);
        appointmentRepository.save(visit.getAppointment());
        updateModal(model, visit);
        return "consult-module/fragments/htmx/diagnoselocationoverview";
    }

    @DeleteMapping("/visit/{visitId}/diagnose/{diagnoseId}")
    String DeleteDiagnose(Model model, @PathVariable Long visitId, @PathVariable Long diagnoseId) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        Diagnose diagnose = diagnoseRepository.findByIdAndPetIdAndAppointmentId(diagnoseId, visit.getPet().getId(), visit.getAppointment().getId()).orElseThrow();
        diagnoseRepository.delete(diagnose);
        updateModal(model, visit);
        return "consult-module/fragments/htmx/diagnoselocationoverview";
    }

    private void updateModal(Model model, Visit visit) {
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository, AutorisationUtils.getCurrentUserMid());
        updatePetDiagnosesInModel(model, diagnoseRepository, AutorisationUtils.getCurrentUserMid(), visit.getPet().getId(), visit.getAppointment().getId());
        model.addAttribute("visit", visit);
    }
}