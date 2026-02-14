package dwe.holding.vmas.controller;


import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.controller.NoteController;
import dwe.holding.customer.client.controller.PetController;
import dwe.holding.customer.client.service.SessionStorageCustomer;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.salesconsult.consult.controller.EstimateController;
import dwe.holding.salesconsult.consult.controller.HtmxSoapAndHistoryController;
import dwe.holding.salesconsult.consult.controller.VisitController;
import dwe.holding.salesconsult.sales.controller.PaymentController;
import dwe.holding.supplyinventory.controller.ReminderController;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ControllerAdvice(assignableTypes = {
        CustomerController.class,
        VisitController.class,
        PetController.class,
        NoteController.class,
        ReminderController.class,
        PaymentController.class,
        EstimateController.class,
        HtmxSoapAndHistoryController.class})
@AllArgsConstructor
public class CustomerPetModelAdvice {
    private final SessionStorageCustomer sessionStorage;
    private final FinancialServiceInterface financialService;

    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            final String customerInfo = sessionStorage.getCustomer().getName();
            final Long customerId = sessionStorage.getCustomer().getId();
            model
                    .addAttribute("customerInformation", customerInfo)
                    .addAttribute("balanceInfo", (customerId == null) ? "" : getInfo(customerId))
            ;
        } catch (
                Exception e) {
            // do nothing, user not yet logged in
        }
    }

    String getInfo(Long customerId) {
        BigDecimal balance = financialService.getCustomerBalance(customerId);
        LocalDate date = financialService.getLastestPaymentDate(customerId);
        if (balance == null || date == null) {
            return "";
        }
        return String.format("E %.2f", balance) + " - " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}