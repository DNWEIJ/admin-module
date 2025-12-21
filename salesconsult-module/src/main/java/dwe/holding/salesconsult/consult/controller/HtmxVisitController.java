package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Diagnose;
import dwe.holding.salesconsult.consult.model.LookupDiagnose;
import dwe.holding.salesconsult.consult.model.LookupLocation;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.DiagnoseRepository;
import dwe.holding.salesconsult.consult.repository.LookupDiagnosesRepository;
import dwe.holding.salesconsult.consult.repository.LookupLocationRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.*;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitController {
    private final LookupDiagnosesRepository lookupDiagnosesRepository;
    private final LookupLocationRepository lookupLocationRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final VisitRepository visitRepository;

    @GetMapping("/visit/diagnose")
    String optionsForDiagnose(@RequestParam boolean ownDiagnoses, Model model) {

        updateDiagnosesInModel(model, lookupDiagnosesRepository,
                ownDiagnoses ? List.of(-1L) : List.of(-1L, AutorisationUtils.getCurrentUserMid())
        );
        return "/consult-module/fragments/htmx/diagnosefield";
    }

    @PostMapping("/visit/{visitId}/diagnose")
    String saveDiagnose(@NotNull Long visitId, Model model, Long diagnoseDropdown_input, Long locationDropdown_input) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId);
        diagnoseRepository.save(Diagnose.builder()
                .lookupDiagnose(LookupDiagnose.builder().id(diagnoseDropdown_input).build())
                .lookupLocation(LookupLocation.builder().id(locationDropdown_input).build())
                .appointment(visit.getAppointment())
                .petId(visit.getPet().getId())
                .build()
        );
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository, List.of(-1L));
        updatePetDiagnosesInModel(model, diagnoseRepository, AutorisationUtils.getCurrentUserMid(), visit.getPet().getId(), visit.getAppointment().getId());
        return "/consult-module/fragments/diagnose";
    }

    @DeleteMapping("/visit/{visitId}/diagnose/{diagnoseId}")
    String DeleteDiagnose(Model model, @PathVariable Long visitId, @PathVariable Long diagnoseId) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId);
        Diagnose diagnose = diagnoseRepository.findByIdAndPetIdAndAppointmentId(diagnoseId, visit.getPet().getId(), visit.getAppointment().getId()).orElseThrow();
        diagnoseRepository.delete(diagnose);
        updatePetDiagnosesInModel(model, diagnoseRepository, AutorisationUtils.getCurrentUserMid(), visit.getPet().getId(), visit.getAppointment().getId());
        return "consult-module/fragments/htmx/diagnoselocationoverview";
    }
}