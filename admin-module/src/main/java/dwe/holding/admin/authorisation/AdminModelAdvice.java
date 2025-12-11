package dwe.holding.admin.authorisation;

import dwe.holding.admin.security.AutorisationUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Slf4j
public class AdminModelAdvice {
    @ModelAttribute
    void modelAdvice(HttpSession session, Model model) {
        try {
            model
                .addAttribute("sessionTimeout", session.getMaxInactiveInterval())
                .addAttribute("localMemberName", AutorisationUtils.getCurrentLocalMemberName())
                .addAttribute("memberShortCode", AutorisationUtils.getCurrentMember().getShortCode())
                .addAttribute("applicationView", AutorisationUtils.getCurrentMember().getApplicationView())
                .addAttribute("applicationName", AutorisationUtils.getCurrentMember().getApplicationName());
        } catch (Exception e) {
            // do nothing, user not yet logged in
        }
    }
}