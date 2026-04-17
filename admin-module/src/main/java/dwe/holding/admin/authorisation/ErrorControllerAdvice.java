package dwe.holding.admin.authorisation;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.io.IOException;
import java.time.LocalDateTime;


// @ControllerAdvice
// @Controller
@Slf4j
public class ErrorControllerAdvice implements ErrorController {


    // @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, HttpServletRequest request, HttpSession session, Model model) {
        // TODO remove for production
        ex.printStackTrace();
        // check and if known, print extra info:
        // ((HandlerMethodValidationException)ex).validationResult.
        log.error("<table><tr><td>Date time</td><td>" + LocalDateTime.now() + "</td></tr>"
                + "<tr><td>request URL</td><td>" + request.getRequestURI() + "</td></tr>"
                + "<tr><td>User</td><td>" + AutorisationUtils.getCurrentUserId() + "</td></tr>"
                + "<tr><td>request issue</td><td>" + ex.getMessage() + "</td></tr>"
                + "<tr><td>request cause</td><td>" + ex.getCause() + "</td></tr></table>");

        model.addAttribute("messageTxt", ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred")
                .addAttribute("messageClass", "alert-error")
                .addAttribute("sessionTimeout", session.getMaxInactiveInterval())
        ;

        boolean isHxRequest = "true".equals(request.getHeader("HX-Request"));
        if (isHxRequest) {
            return "admin-module/modal/message::notification";
        }
        return "admin-module/error";
    }

    // --- Handle default error path ---
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, HttpSession session, HttpServletResponse response) throws IOException {

        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exceptionObj = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String message = "Unknown error";

        if (statusCode.hashCode() == 401 || statusCode.hashCode() == 403) {
            response.sendRedirect("/admin/login");
        }

        if (exceptionObj instanceof Exception ex) {
            message = ex.getMessage();
        } else if (statusCode != null) {
            message = "Error code: " + statusCode;
        }

            model.addAttribute("message", message)
                    .addAttribute("messageClass", "alert-error")
                    .addAttribute("sessionTimeout", session.getMaxInactiveInterval())
            ;

            boolean isHxRequest = "true".equals(request.getHeader("HX-Request"));

            if (isHxRequest) {
                return "admin-module/modal/message::notification";
            }
            return "admin-module/error";
        }
    }
