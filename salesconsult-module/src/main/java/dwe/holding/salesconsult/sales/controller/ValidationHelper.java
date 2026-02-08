package dwe.holding.salesconsult.sales.controller;

import dwe.holding.salesconsult.consult.model.Appointment;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ValidationHelper {

    public static boolean validateAppointmenIsOk(Appointment app, RedirectAttributes redirect) {
        if (app.isCancelled() || app.isCompleted()) {
            redirect.addFlashAttribute("message", "Appointment is cancelled or completed.");
            return false;
        }
        return true;
    }
}
