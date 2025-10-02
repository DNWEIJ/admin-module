package dwe.holding.generic.app.preferences.controller;


import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.app.preferences.model.VmasPreferences;
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