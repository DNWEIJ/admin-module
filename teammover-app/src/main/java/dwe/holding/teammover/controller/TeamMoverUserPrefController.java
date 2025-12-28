package dwe.holding.teammover.controller;

import dwe.holding.admin.authorisation.tenant.localmember.LocalMemberRepository;
import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.teammover.model.TeamMoverUserPreferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

import java.util.Comparator;


@Controller
@RequestMapping("/teammover")
public class TeamMoverUserPrefController {

    private final LocalMemberRepository localMemberRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public TeamMoverUserPrefController(LocalMemberRepository localMemberRepository, UserService userService, ObjectMapper objectMapper) {
        this.localMemberRepository = localMemberRepository;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/userpreferences")
    String loadUserPreferences(Model model)  {
        model.addAttribute("localMembersList",
                localMemberRepository.findByMemberId(AutorisationUtils.getCurrentUserMid())
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
    String localMember(SettingsForm form) {
        form.userPreferences.setNrOfTeamMembers(form.userPreferences.getNames().size());

        userService.saveUserSettings(objectMapper.writeValueAsString(form.userPreferences));
        return "redirect:/admin/index"; // required to redirect to the index to finish the flow of settings for initial login
    }

    record SettingsForm(String id, TeamMoverUserPreferences userPreferences) {
    }
}