package dwe.holding.vmas.agenda;

import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.vmas.model.CalendarEvent;
import dwe.holding.vmas.model.VmasUserPreferences;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dwe.holding.vmas.agenda.AgendaHelper.fromAppointmentList;

@RestController
@RequestMapping("/agenda/task")
@AllArgsConstructor
public class EventAndResourceTaskController {
    private final AppointmentRepository appointmentRepository;
    private final VisitRepository visitRepository;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @PostMapping("/events")
    public List<CalendarEvent> returnEventData(AgendaHelper.DataForm dataForm, Locale locale) {
        return createList(dataForm, locale,
                appointmentRepository.findByVisitDateTimeBetweenAndLocalMemberId(dataForm.start(), dataForm.end(), dataForm.localMemberId())
        );
    }

    @PostMapping("/resources")
    public String returnEventResources(AgendaHelper.DataForm dataForm) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        LocalMemberPreferences prefMemberData = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);

        return switch (dataForm.agendaType()) {
            case Room ->
                    createResources(prefMemberData.getRoom1(), prefMemberData.getRoom2(), prefMemberData.getRoom3());
            case Vet -> createResources(prefData.getAgendaVet1(), prefData.getAgendaVet2(), prefData.getAgendaVet3());
            case Week -> "";
        };
    }

    @PostMapping("/appointment/{id}")
    public List<CalendarEvent> getEventForAppointment(@PathVariable Long id, AgendaHelper.DataForm dataForm, Locale locale) {
        Appointment app = appointmentRepository.findByIdAndMemberId(id,AutorisationUtils.getCurrentUserMid()).orElseThrow();
        return createList(dataForm, locale, List.of(app));
    }

    @PostMapping("/visit")
    public List<CalendarEvent> updateVisit(AgendaHelper.DataForm dataForm, @NotNull Long visitId, @NotBlank @RequestParam String option, Locale locale) {

        if (option.equals("cancel")) {
            Visit visit = visitRepository.findById(visitId).orElseThrow();
            visit.getAppointment().setCancelled(YesNoEnum.Yes);
            Appointment appointment = appointmentRepository.save(visit.getAppointment());

            return createList(dataForm, locale, List.of(appointment));
        } else {
            Visit visit = visitRepository.findById(visitId).orElseThrow();
            if (VisitStatusEnum.WAITING.equals(visit.getStatus()) || VisitStatusEnum.PLANNED.equals(visit.getStatus())) {
                if (option.equalsIgnoreCase(VisitStatusEnum.PLANNED.toString()))
                    visit.setStatus(VisitStatusEnum.PLANNED);
                if (option.equalsIgnoreCase(VisitStatusEnum.WAITING.toString()))
                    visit.setStatus(VisitStatusEnum.WAITING);
                visit = visitRepository.save(visit);
                return createList(dataForm, locale, List.of(appointmentRepository.findById(visit.getAppointment().getId()).orElseThrow()));
            }
            return null;
        }
    }

    private List<CalendarEvent> createList(AgendaHelper.DataForm dataForm, Locale locale, List<Appointment> appointments) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        LocalMemberPreferences prefMemberData = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);

        Set<String> excludedStaff = Stream.of(
                prefData.getAgendaVet1(), prefData.getAgendaVet2(), prefData.getAgendaVet3()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Set<String> excludedRooms = Stream.of(
                prefMemberData.getRoom1(), prefMemberData.getRoom2(), prefMemberData.getRoom3()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        return fromAppointmentList(appointments, locale, messageSource, dataForm, excludedRooms, excludedStaff
        );
    }

    private String createResources(String row1, String row2, String row3) {
        String template = """
                {"id": "%s", "title": "%s"}
                """;
        return "[" + template.formatted(row1, row1)
                + "," + template.formatted(row2, row2)
                + "," + template.formatted(row3, row3)
                + "," + template.formatted("other", "other")
                + "]";
    }


}
