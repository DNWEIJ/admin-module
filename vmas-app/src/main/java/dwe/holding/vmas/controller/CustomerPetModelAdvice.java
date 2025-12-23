package dwe.holding.vmas.controller;


import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.controller.NoteController;
import dwe.holding.customer.client.controller.PetController;
import dwe.holding.supplyinventory.controller.ReminderController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {CustomerController.class, PetController.class, NoteController.class, ReminderController.class})
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