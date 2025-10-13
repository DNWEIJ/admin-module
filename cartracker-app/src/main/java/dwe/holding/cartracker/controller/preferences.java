package dwe.holding.cartracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class preferences {
    @GetMapping("/cartracker/userpreferences")
    public String getPreferences() {
        return "cartracker-module/userpreferences";
    }
}