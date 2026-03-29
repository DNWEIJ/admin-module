package dwe.holding.vmas.controller;


import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.controller.NoteController;
import dwe.holding.customer.client.controller.PetController;
import dwe.holding.customer.client.service.CustomerFinancialInfo;
import dwe.holding.salesconsult.consult.controller.EstimateController;
import dwe.holding.salesconsult.consult.controller.HtmxSoapAndHistoryController;
import dwe.holding.salesconsult.consult.controller.VisitController;
import dwe.holding.salesconsult.sales.controller.HtmxAddPaymentController;
import dwe.holding.salesconsult.sales.controller.PaymentController;
import dwe.holding.salesconsult.sales.controller.RefundController;
import dwe.holding.salesconsult.sales.controller.otc.OTCSellController;
import dwe.holding.supplyinventory.controller.ReminderController;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {
        CustomerController.class,
        VisitController.class,
        PetController.class,
        NoteController.class,
        ReminderController.class,
        PaymentController.class,
        EstimateController.class,
        OTCSellController.class,
        HtmxSoapAndHistoryController.class,
        HtmxAddPaymentController.class,
        RefundController.class})
@AllArgsConstructor
public class CustomerPetModelAdvice {

    private final CustomerFinancialInfo customerFinancialInfo;

    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            customerFinancialInfo.setToModel(model);
        } catch (
                Exception e) {
            // do nothing, user not yet logged in
        }
    }
}