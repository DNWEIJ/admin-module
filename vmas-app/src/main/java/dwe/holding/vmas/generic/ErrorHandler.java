package dwe.holding.vmas.generic;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handle(RuntimeException ex,
                         HttpServletRequest request,
                         RedirectAttributes ra) {

        ra.addFlashAttribute("error", ex.getMessage());

        String lastUrl = (String) request.getSession()
                .getAttribute("LAST_GET_URL");

        return "redirect:" + (lastUrl != null ? lastUrl : "/");
    }


}