package dwe.holding.teammover.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.generic.admin.authorisation.member.LocalMemberRepository;
import dwe.holding.generic.admin.expose.UserPreferencesService;
import dwe.holding.generic.admin.model.PresentationFunction;
import dwe.holding.generic.admin.security.AutorisationUtils;
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

    @PostMapping("/userpreferences")
    String localMember(SettingsForm form, Model model) throws JsonProcessingException {
        userPreferencesService.storeAppPreferences(  Long.parseLong(form.id), form.userPreferences);
        return "redirect:/index"; // required to redirect to the index to finish the flow of settings for initial login
    }
    record SettingsForm(String id, TeamMoverUserPreferences userPreferences) {
    }
}