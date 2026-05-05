package dwe.holding.vmas.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.vmas.model.VmasUserPreferences;
import dwe.holding.vmas.model.enums.ColorEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    private final CustomerForm customerForm;

    // Called for new users via login controller. Afterward it can be called on users request
    @GetMapping("/userpreferences")
    String loadUserPreferences(Model model, HttpServletRequest request) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        model
                .addAttribute("_csrf", request.getAttribute(CsrfToken.class.getName()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("user", AutorisationUtils.getCurrentUserSettings())
                .addAttribute("userPreferences", prefData)
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("languageList", LanguagePrefEnum.getWebList());
        return "vmas-module/userpreferences";
    }

    @PostMapping("/userpreferences")
    String localMember(@Valid SettingsForm form) {
        // color is set via a separate POST, so we need to add that color, not from the form
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        form.userPreferences.setColor(prefData.getColor());

        userService.saveUserSettings(objectMapper.writeValueAsString(form.userPreferences), form.localMemberId(), form.language(), form.username(), form.email());
        customerForm.updateForm(
                form.userPreferences.getSearchCustStart().booleanValue(),
                form.userPreferences.getSearchCustStreet().booleanValue(),
                form.userPreferences.getSearchCustNameTelephone().booleanValue(),
                form.userPreferences.getSearchCustPet().booleanValue()
        );

        return "redirect:/generic/index"; // required to redirect to the index to finish the flow of settings for initial login
    }

    @PostMapping("/userpreferences/color")
    String color(@NotNull ColorEnum color) {
        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
        prefData.setColor(color.getValue());
        userService.saveUserSettings(objectMapper.writeValueAsString(prefData));
        return "fragments/elements/empty";
    }

    record SettingsForm(Long localMemberId, LanguagePrefEnum language, String username, String email, VmasUserPreferences userPreferences) {
    }
}