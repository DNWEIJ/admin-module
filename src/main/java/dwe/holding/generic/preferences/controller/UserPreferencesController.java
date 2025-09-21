package dwe.holding.generic.preferences.controller;


import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.preferences.model.UserPreferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPreferencesController {
    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferencesController(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @GetMapping("/userpreferences")
    String getUserPreferences(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("userPref", new UserPreferences());
        setModelData(model);
        return "app-module/userpreferences";
    }

    private void setModelData(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }

}