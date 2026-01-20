package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.model.Payment;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
@RequestMapping("/sales")
public class HtmxAddPaymentController {
    PaymentController paymentController;
    VisitRepository visitRepository;

    @GetMapping("/visit/{visitId}/payment")
    String getPaymentForModal(@PathVariable Long visitId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();

        if (visit.isOpen()) {
            paymentController.getPaymentRecord(visit.getPet().getCustomer().getId(), model);
        }
        return "sales-module/payment/htmx/paymentmodal";
    }

    @PostMapping("/customer/{customerId}/visit/payment")
    String savePayment(@PathVariable Long customerId, Payment paymentForm, RedirectAttributes redirect, HttpServletResponse response) {
        paymentController.newOrUpdateRecord(customerId, paymentForm, redirect);
        response.setHeader("HX-Trigger", "closeModal");
        return "fragments/elements/empty";
    }
}