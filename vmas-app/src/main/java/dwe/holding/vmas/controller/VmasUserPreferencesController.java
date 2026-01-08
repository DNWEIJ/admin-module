package dwe.holding.vmas.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.vmas.model.VmasUserPreferences;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

@RequestMapping("/vmas")
@Controller
@AllArgsConstructor
public class VmasUserPreferencesController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    // Called for new users via login controller. Afterward it can be called on users request
    @GetMapping("/userpreferences")
    String loadUserPreferences(Model model, HttpServletRequest request) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class);
        model
                .addAttribute("_csrf", request.getAttribute(CsrfToken.class.getName()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("user", AutorisationUtils.getCurrentUserSettings())
                .addAttribute("userPreferences", prefData == null ? new VmasUserPreferences() : prefData)
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("languageList", LanguagePrefEnum.getWebList());
        return "vmas-module/userpreferences";
    }

    @PostMapping("/userpreferences")
    String localMember(@Valid SettingsForm form) {
        userService.saveUserSettings(objectMapper.writeValueAsString(form.userPreferences), form.localMemberId(), form.language(), form.username(), form.email());
        return "redirect:/admin/index"; // required to redirect to the index to finish the flow of settings for initial login
    }

    record SettingsForm(Long localMemberId, LanguagePrefEnum language, String username, String email, VmasUserPreferences userPreferences) {
    }
}