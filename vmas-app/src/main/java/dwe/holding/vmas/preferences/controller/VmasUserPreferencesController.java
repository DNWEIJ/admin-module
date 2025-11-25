package dwe.holding.vmas.preferences.controller;


import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.vmas.preferences.model.VmasPreferences;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class VmasUserPreferencesController {


    @GetMapping("/userpreferences")
    String getUserPreferences(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("userPref", new VmasPreferences());
        setModelData(model);
        return "app-module/userpreferences";
    }

    private void setModelData(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }

}