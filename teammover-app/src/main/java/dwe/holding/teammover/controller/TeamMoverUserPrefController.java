package dwe.holding.teammover.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.admin.authorisation.member.LocalMemberRepository;
import dwe.holding.admin.expose.UserPreferencesService;
import dwe.holding.admin.model.UserPreferences;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.teammover.model.TeamMoverUserPreferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;


@Controller
@RequestMapping("/teammover")
public class TeamMoverUserPrefController {

    private final LocalMemberRepository localMemberRepository;
    private final UserPreferencesService userPreferencesService;
    private final ObjectMapper objectMapper;

    public TeamMoverUserPrefController(LocalMemberRepository localMemberRepository, UserPreferencesService userPreferencesService, ObjectMapper objectMapper) {
        this.localMemberRepository = localMemberRepository;
        this.userPreferencesService = userPreferencesService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/userpreferences")
    String loadUserPreferences(Model model) throws JsonProcessingException {
        model.addAttribute("localMembersList",
                localMemberRepository.findByMember_Id(AutorisationUtils.getCurrentUserMid())
                        .stream().map(
                                f -> new PresentationElement(f.getId(), f.getLocalMemberName(), true)
                        )
                        .sorted(Comparator.comparing(PresentationElement::getName)).toList()
        );
        model.addAttribute("memberLocalId", AutorisationUtils.getCurrentUserMlid());
        model.addAttribute("names", objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), TeamMoverUserPreferences.class).getNames());
        return AutorisationUtils.getCurrentMember().getApplicationView() + "/userpreferences";
    }

    @PostMapping("/userpreferences")
    String localMember(SettingsForm form) throws JsonProcessingException {
        form.userPreferences.setNrOfTeamMembers(form.userPreferences.getNames().size());

        userPreferencesService.storeAppPreferences(Long.parseLong(form.id), objectMapper.writeValueAsString(form.userPreferences));
        return "redirect:/admin/index"; // required to redirect to the index to finish the flow of settings for initial login
    }

    record SettingsForm(String id, TeamMoverUserPreferences userPreferences) {
    }
}