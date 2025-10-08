package dwe.holding.generic.cartracker.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {TripController.class, OverviewController.class})
public class CarTrackerModelAdvice {

    @ModelAttribute
    void modelAdvice(Model model) {
        model.addAttribute("applicationName", "cartracker-module");
    }
}