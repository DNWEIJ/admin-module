package dwe.holding.vmas.controller;


import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.vmas.model.VmasPreferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("vmas")
@Controller
public class VmasUserPreferencesController {


    @GetMapping("/userpreferences")
    String getUserPreferences(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("userPref", new VmasPreferences());
        setModelData(model);
        return "vmas-module/userpreferences";
    }

    private void setModelData(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }

}