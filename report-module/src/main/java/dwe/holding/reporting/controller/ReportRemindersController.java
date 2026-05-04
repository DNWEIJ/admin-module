package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Reminder;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.reporting.model.DocumentReportType;
import dwe.holding.reporting.model.ReportTemplate;
import dwe.holding.reporting.repository.DocumentTemplateRepository;
import dwe.holding.reporting.repository.dsl.ReminderListDsl;
import dwe.holding.reporting.repository.projection.RemindersListProjection;
import dwe.holding.reporting.service.Counter;
import dwe.holding.reporting.service.ReminderGenerateEmailAndSendOrCreateReport;
import dwe.holding.reporting.service.SessionStorageReporting;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.consult.repository.VisitProjection;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.comparing;

@RequestMapping("report")
@AllArgsConstructor
@Controller
@Slf4j
public class ReportRemindersController {
    private final ReminderListDsl reminderListDsl;
    private final ReminderRepository reminderRepository;
    private final LookupSpeciesRepository lookupSpeciesRepository;
    private final SessionStorageReporting sessionStorageReporting;
    private final ObjectMapper objectMapper;
    private final ReminderGenerateEmailAndSendOrCreateReport reminderGenerateEmailAndSendOrCreateReport;
    private final DocumentTemplateRepository documentTemplateRepository;
    private final AppointmentRepository appointmentRepository;
    private final VisitRepository visitRepository;

    @GetMapping("reminders")
    public String reportReminders(Model model, ReminderListForm reminderForm) {
        if (reminderForm == null || reminderForm.from == null) {
            reminderForm = getForm();
        }
        model
                .addAttribute("reminderForm", reminderForm)
                .addAttribute("reminderTypes", reminderRepository.findDistinctReminderTextByMemberIdOrderByReminderText(AutorisationUtils.getCurrentUserMid())
                        .stream().map(reminder -> new PresentationElement(reminder, reminder, true)).toList()
                )
                .addAttribute("species", lookupSpeciesRepository.findByMemberId(AutorisationUtils.getCurrentUserMid())
                        .stream().map(f -> new PresentationElement(f.getId(), f.getSpecy()))
                        .sorted(comparing(PresentationElement::getName)).toList()
                )
                .addAttribute("reminders", getReminders(reminderForm))
        ;
        return "reporting-module/reporting/reminders";
    }

    @PostMapping("reminders")
    public String startProcessingSearchResults(Model model, @RequestParam List<Long> reminderSelected, SessionStorageReporting.ActionType actionType) {
        // save the result for later, start generic writing the email/letter
        switch (actionType) {
            case REPORT, EMAIL -> sessionStorageReporting.setReporting(
                    new SessionStorageReporting.ReportingSettings(SessionStorageReporting.ReportTypePage.REMINDER, actionType, "/report/reminders", objectMapper.writeValueAsString(reminderSelected))
            );
            case LATEST_CONSULT, CONSULT -> {
                Reminder reminder = reminderRepository.findById(reminderSelected.getFirst()).orElseThrow();
                Visit visit = null;
                if (reminder.getOriginatingAppointmentId().equals(-1)) {
                    VisitProjection visitProjection = visitRepository.findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(AutorisationUtils.getCurrentUserMid(), List.of(reminder.getPet().getId()))
                            .stream()
                            .filter(v -> v.appointmentVisitDateTime() != null)
                            .min(comparing(v ->
                                    Math.abs(Duration.between(reminder.getDueDate(), v.appointmentVisitDateTime()).toSeconds())))
                            .orElse(null);
                    visit= new Visit();
                    visit.setId(visitProjection.id());
                } else {
                    Appointment app = appointmentRepository.findByIdAndMemberId(reminder.getOriginatingAppointmentId(), AutorisationUtils.getCurrentUserMid()).orElseThrow();
                    visit = app.getVisits().stream().filter(vist -> vist.getPet().getId().equals(reminder.getPet().getId())).findFirst().orElseThrow();
                }
                return "redirect:/consult/visit/customer/" + reminder.getPet().getCustomer().getId() + "/visit/" + visit.getId();
            }
        }
        return "redirect:/report/process/selectreport?reportType=" + SessionStorageReporting.ReportTypePage.REMINDER + "&actionType=" + actionType.name();
    }

    @PostMapping("/reminders/selectedreport/action")
    String previewOrAction(Model model, Long templateId, String content, String submitButton, HttpSession session) {
        final ReportTemplate template = documentTemplateRepository.findById(templateId).orElseThrow();
        final ReportTemplate mainTemplate = documentTemplateRepository.findByReportType(DocumentReportType.EMAIL_MASTER).stream().findFirst().orElseThrow();

        template.setContent(content);
        SessionStorageReporting.ReportingSettings reportSettings = sessionStorageReporting.getReporting();
        List<Long> reminders = objectMapper.readValue(reportSettings.getData(), new TypeReference<>() { });
        // preview will open in a different TAB (frontend driven)
        if (submitButton.equals("_preview")) {
            reminders = List.of(reminders.get(0));
        }
        session.setAttribute(Counter.name, new Counter(reminders.size()));

        if (SessionStorageReporting.ActionType.REPORT.equals(reportSettings.getActionType()) || submitButton.equals("_preview")) {
            // report will open in a different TAB (frontend driven)
            ReminderGenerateEmailAndSendOrCreateReport.ResultAndError result = reminderGenerateEmailAndSendOrCreateReport.createHtmlAndSend(mainTemplate, reminders, template,  submitButton.equals("_preview"), (Counter) session.getAttribute(Counter.name), reportSettings.getActionType());
            model.addAttribute("elementList", result.reports());
            return "reporting-module/print/elementlist";
        } else {
            model.addAttribute("processingEmails", true);
            reminderGenerateEmailAndSendOrCreateReport.createHtmlAndSendAsync(mainTemplate, reminders, template, false, (Counter) session.getAttribute(Counter.name), reportSettings.getActionType());
        }
        return reportReminders(model, getForm());
    }


    @GetMapping("/reminders/selectedreport/status")
    String showStatusOfProcessingEmailsHtmx(Model model, HttpSession session) {
        Counter counter = (Counter) session.getAttribute(Counter.name);
        model.addAttribute("flatData",
                String.format(
                        """
                                <span data-counter="%s"><progress value="%d" max="%d">Done!</progress></span>
                                """.formatted(counter.isFinished(), counter.getIncrementValue(), counter.getMaxValue())
                ));
        return "fragments/elements/flatData";
    }

    ReminderListForm getForm() {
        return new ReminderListForm(LocalDate.now(), LocalDate.now().plusDays(1),
                true, "", "all", List.of(), 0, List.of(), 0
        );
    }

    List<RemindersListProjection> getReminders(ReminderListForm reminderForm) {
        return reminderListDsl.findReminders(AutorisationUtils.getCurrentUserMid(),
                reminderForm.from, reminderForm.includeTill,
                (reminderForm.species == null || reminderForm.species.size() == reminderForm.speciesListLength) ? List.of() : reminderForm.species,
                (reminderForm.reminderTypes == null || reminderForm.reminderTypes.size() == reminderForm.speciesListLength) ? List.of() : reminderForm.reminderTypes,
                reminderForm.dueDateType, reminderForm.lastNameFilter, reminderForm.onlyActiveWithLivePet == null ? Boolean.FALSE : reminderForm.onlyActiveWithLivePet);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ReminderListForm {
        private LocalDate from;
        private LocalDate includeTill;
        private Boolean onlyActiveWithLivePet;
        private String lastNameFilter;
        private String dueDateType;
        private List<String> species;
        private Integer speciesListLength;
        private List<String> reminderTypes;
        private Integer reminderTypesLength;
    }
}