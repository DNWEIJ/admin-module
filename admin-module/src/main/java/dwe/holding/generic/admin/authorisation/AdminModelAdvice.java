package dwe.holding.generic.admin.authorisation;

import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AdminModelAdvice {
    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            model.addAttribute("localMemberName", AutorisationUtils.getCurrentLocalMemberName());
            model.addAttribute("memberShortCode", AutorisationUtils.getCurrentMember().getShortCode());
            model.addAttribute("applicationView", AutorisationUtils.getCurrentMember().getApplicationView());
            model.addAttribute("applicationName", AutorisationUtils.getCurrentMember().getApplicationName());
        } catch (Exception e){
            // do nothing, user not yet logged in
        }
    }
}