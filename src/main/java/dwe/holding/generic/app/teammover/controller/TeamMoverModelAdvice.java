package dwe.holding.generic.app.teammover.controller;

import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {GameController.class, DriverController.class})
public class TeamMoverModelAdvice {

    @ModelAttribute
    void modelAdvice(Model model) {
        model.addAttribute("localMemberName", AutorisationUtils.getCurrentLocalMemberName());
        model.addAttribute("memberShortCode", AutorisationUtils.getCurrentMember().getShortCode());
        model.addAttribute("applicationName", "teammover-module");
    }
}