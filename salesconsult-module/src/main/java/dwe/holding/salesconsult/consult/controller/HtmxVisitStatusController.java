package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateVisitStatusInModel;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxVisitStatusController {
    private final VisitRepository visitRepository;
    private final CustomerService customerService;
    private final AppointmentVisitService appointmentVisitService;

    @PostMapping("/visit/{visitId}/updatestatus")
    String updateVisitStatusHtmx(@PathVariable Long visitId, Model model, String changeStatusTo,  HttpServletResponse response) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();

        if (!visit.isOpen()) {
            updateVisitStatusInModel(model, visit.getStatus());
            model.addAttribute("visit", visit);
            model.addAttribute("message", "Something went wrong. Please try again; or refresh browser");
            return "sales-module/fragments/htmx/visitstatus::closedVisitStatus";
        }
        boolean pageRefreshNeeded = false;
        visit.setStatus(VisitStatusEnum.valueOf(changeStatusTo));
        if (visit.getStatus().equals(VisitStatusEnum.FINISHED)) {
            if (
                    visit.getAppointment().getVisits().stream()
                            .allMatch(visitFromList -> VisitStatusEnum.FINISHED.equals(visitFromList.getStatus())
                            )
            ) {
                visit.getAppointment().setCompleted(YesNoEnum.Yes);
                pageRefreshNeeded = true;
            }
        }
        visit = visitRepository.save(visit);
        updateVisitStatusInModel(model, visit.getStatus());
        model.addAttribute("visit", visit);
        if (pageRefreshNeeded) response.setHeader("HX-Trigger", "refreshPage");
        return "consult-module/fragments/htmx/actionbar";
    }

    @PostMapping("/visit/{visitId}/updatestatus/{action}")
    public String updateVisitAppointmentStatus(@PathVariable Long visitId, @PathVariable String action, HttpServletResponse response) {

        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();

        if (visit.isOpen()) {
            switch (action.toLowerCase()) {
                case "cancel" -> visit.getAppointment().setCancelled(YesNoEnum.Yes);
                case "complete" -> visit.getAppointment().setCompleted(YesNoEnum.Yes);
                default -> throw new IllegalArgumentException("Unknown action: " + action);
            }
            visit = visitRepository.save(visit);
        } else {
            if (action.toLowerCase().equals("reactivate")) {
                checkAndUpdate(visit);
            }
            visit = visitRepository.save(visit);
        }
        response.setHeader("HX-Trigger", "refreshPage");
        return "fragments/elements/empty";
    }

    @GetMapping("/visit/{visitId}/reschedule")
    public String rescheduleAppointment(@PathVariable Long visitId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (visit.isOpen()) {
            model
                    .addAttribute("appointment", visit.getAppointment())
                    .addAttribute("visitId", visit.getId())
                    .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList());
            return "consult-module/fragments/htmx/datelocationmodal";
        }
        return "consult-module/fragments/htmx/datelocationmodal";
    }

    @PostMapping("/visit/{visitId}/reschedule")
    public String saveRescheduleAppointment(@PathVariable Long visitId, VisitController.CreateVisitForm createVisitForm, HttpServletResponse response, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        if (visit.isOpen()) {
            appointmentVisitService.saveAppointment(visit.getAppointment().getId(), createVisitForm.appointment().getVisitDateTime(), createVisitForm.appointment().getLocalMemberId());

            model.addAttribute("message", "Reschedule saved successfully");
            response.setHeader("HX-Trigger", "closeModal");
            return "fragments/elements/empty";
        }
        response.setHeader("HX-Trigger", "closeModal");
        return "fragments/elements/empty";
    }

    private void checkAndUpdate(Visit visit) {
        if (!visit.isOpen()) {
            visit.getAppointment().setCancelled(YesNoEnum.No);
            visit.getAppointment().setCompleted(YesNoEnum.No);
            visit.setStatus(VisitStatusEnum.PAYMENT);
        }
    }

}