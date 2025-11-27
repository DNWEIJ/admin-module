package dwe.holding.admin.authorisation;

import dwe.holding.admin.security.AutorisationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Slf4j
public class AdminModelAdvice {
    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            log.info("adminModelAdvice called");
            model.addAttribute("localMemberName", AutorisationUtils.getCurrentLocalMemberName());
            model.addAttribute("memberShortCode", AutorisationUtils.getCurrentMember().getShortCode());
            model.addAttribute("applicationView", AutorisationUtils.getCurrentMember().getApplicationView());
            model.addAttribute("applicationName", AutorisationUtils.getCurrentMember().getApplicationName());
        } catch (Exception e){
            // do nothing, user not yet logged in
        }
    }
}