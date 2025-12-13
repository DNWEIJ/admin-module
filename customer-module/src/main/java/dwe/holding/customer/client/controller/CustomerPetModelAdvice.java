package dwe.holding.customer.client.controller;


import dwe.holding.admin.security.AutorisationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {CustomerController.class, PetController.class, NotesController.class, ReminderController.class})
public class CustomerPetModelAdvice {
    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            model.addAttribute("customerInformation", AutorisationUtils.getTempGenericStorage());
        } catch (Exception e) {
            // do nothing, user not yet logged in
        }
    }
}