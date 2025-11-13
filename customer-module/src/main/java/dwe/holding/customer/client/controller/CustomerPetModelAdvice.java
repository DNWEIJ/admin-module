package dwe.holding.customer.client.controller;


import dwe.holding.customer.client.model.Customer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {CustomerController.class, PetController.class, NotesController.class, ReminderController.class})
public class CustomerPetModelAdvice {
    @ModelAttribute
    void modelAdvice(Model model) {
        try {
// TODO AutorisationUtils.getInfoObject()
            model.addAttribute("customerInformation",
                    Customer.builder().id(123L).firstName("Daniel").lastName("Weijers").surName("van der").middleInitial("D.N.").title("Dr.").build().getCustomerNameWithId()
            );
        } catch (Exception e) {
            // do nothing, user not yet logged in
        }
    }
}