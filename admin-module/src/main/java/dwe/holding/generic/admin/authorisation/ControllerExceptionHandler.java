package dwe.holding.generic.admin.authorisation;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ControllerExceptionHandler {
//    @ExceptionHandler(HsqlException.class)
//    public String handleConstraintViolation(HsqlException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("globalError", "Er is een fout opgetreden: deze gegevens bestaan al.");
//        String referer = request.getHeader("Referer");
//        return "redirect:" + referer;
//    }
}