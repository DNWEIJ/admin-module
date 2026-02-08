package dwe.holding.vmas.agenda;

import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.security.AutorisationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequestMapping("/agenda")
@AllArgsConstructor
@Controller
public class AgendaController {

    private final ObjectMapper objectMapper;

    @GetMapping("/agenda")
    public String returnCalendarJs(Model model) {

        LocalMemberPreferences prefMemberData = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);
        LocalDateTime date = LocalDateTime.now();

        model
                .addAttribute("initialDate", date.toLocalDate().toString())
                .addAttribute("selectDate", selectDate(date.toLocalDate().toString()))
                .addAttribute("scrollToTime", date.minusHours(1).format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .addAttribute("agendaType", prefMemberData.getRoomAgenda().toString())
                .addAttribute("locationDropdown", locationDropDown())
                .addAttribute("colorModal", colorModalButton())
                .addAttribute("changeRoomVet", roomVetDropDown())
                .addAttribute("localMemberId", AutorisationUtils.getCurrentUserMlid())
        ;

        return "agenda-module/agenda";
    }

    private String selectDate(String date) {
        return """
                <input type="date" id="selectDate" value="%s" name="selectDate" onchange="agendaState.changeDate(this.value)"/>
                """.formatted(date);
    }

    private String roomVetDropDown() {
        return """
                <select id="agendaType" name="agendaType" onchange="agendaState.changeResourceType(this.value)" required="required">
                    <option value="Room">Room</option>
                    <option value="Vet">Vet</option>
                </select>
                """;
    }

    private String colorModalButton() {
        return """
                <input type="button" aria-label="Close" value="Colors" rel="prev" data-target="statusColourDisplay" onclick="toggleModal(event)">
                """;
    }

    private String locationDropDown() {
        Long currentLocalMemberId = AutorisationUtils.getCurrentUserMlid();
        Map<Long, String> map = AutorisationUtils.getLocalMemberMap(); // id, name for option

        String option = """
                <option value="%s" %s>%s</option>
                """;

        String str = """
                <select id="localMemberId" name="localMemberId" onchange="agendaState.changeLocation(this.value)" required="required">
                    %s
                </select>
                """;

        String options = map.entrySet().stream()
                .map(entry -> option.formatted(
                        entry.getKey(),
                        entry.getKey().equals(currentLocalMemberId) ? "selected" : "",
                        entry.getValue()
                ))
                .reduce("", String::concat);

        return str.formatted(options);
    }
}

