package dwe.holding.generic.app.teammover.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.generic.admin.autorisation.member.LocalMemberRepository;
import dwe.holding.generic.admin.expose.UserPreferencesService;
import dwe.holding.generic.admin.model.PresentationFunction;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.app.teammover.model.TeamMoverUserPreferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.UUID;

@Controller
public class TeamMoverUserPrefController {

    private final LocalMemberRepository localMemberRepository;
    private final UserPreferencesService userPreferencesService;
    private final ObjectMapper objectMapper;

    public TeamMoverUserPrefController(LocalMemberRepository localMemberRepository, UserPreferencesService userPreferencesService, ObjectMapper objectMapper) {
        this.localMemberRepository = localMemberRepository;
        this.userPreferencesService = userPreferencesService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/teammover/userpreferences")
    String loadUserPreferences(Model model) {
        model.addAttribute("localMembersList",
                localMemberRepository.findByMember_Id(AutorisationUtils.getCurrentUserMid())
                        .stream().map(
                                f -> new PresentationFunction(f.getId(), f.getLocalMemberName(), true)
                        )
                        .sorted(Comparator.comparing(PresentationFunction::getName)).toList()
        );
        model.addAttribute("memberLocalId", AutorisationUtils.getCurrentUserMlid());
        model.addAttribute("nrOfTeamMembers", objectMapper.convertValue(AutorisationUtils.getCurrentUserPref(), TeamMoverUserPreferences.class).getNrOfTeamMembers());
        return AutorisationUtils.getCurrentMember().getApplicationView() + "/userpreferences";
    }

    @PostMapping("/teammover/userpreferences")
    String localMember(SettingsForm form, Model model) throws JsonProcessingException {
        userPreferencesService.storeAppPreferences(UUID.fromString(form.id), form.userPreferences);
        return "redirect:/index"; // required to redirect to the index to finish the flow of settings for initial login
    }

    record SettingsForm(String id, TeamMoverUserPreferences userPreferences) {
    }
}