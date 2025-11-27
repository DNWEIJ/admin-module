package dwe.holding.admin.authorisation;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public String handleConstraintViolation(RuntimeException ex, RedirectAttributes redirectAttributes) {
        log.error("Error occurred", ex.getCause());
        redirectAttributes.addFlashAttribute("globalError", "Something went wrong!");
        return "redirect:/admin/login";
    }
}