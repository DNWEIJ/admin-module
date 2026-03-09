package dwe.holding.vmas.agenda;

import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.vmas.model.CalendarEvent;
import dwe.holding.vmas.model.VmasUserPreferences;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;

import static dwe.holding.vmas.agenda.EventAndResourceController.createList;
import static dwe.holding.vmas.agenda.EventAndResourceController.createResources;

@RestController
@RequestMapping("/agenda/task")
@AllArgsConstructor
public class EventAndResourceTaskController {
    private final AppointmentRepository appointmentRepository;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @PostMapping("/events")
    public List<CalendarEvent> returnEventData(AgendaHelper.DataForm dataForm, Locale locale) {
        return createList(objectMapper, messageSource, dataForm, locale, appointmentRepository.findByVisitDateTimeBetweenAndLocalMemberId(dataForm.start(), dataForm.end(), dataForm.localMemberId()));
    }

    @PostMapping("/resources")
    public String returnEventResources(AgendaHelper.DataForm dataForm) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        LocalMemberPreferences prefMemberData = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);

        return switch (dataForm.agendaType()) {
            case Room -> createResources(prefMemberData.getRoom1(), prefMemberData.getRoom2(), prefMemberData.getRoom3());
            case Vet -> createResources(prefData.getAgendaVet1(), prefData.getAgendaVet2(), prefData.getAgendaVet3());
            case Week -> "";
        };
    }
}
