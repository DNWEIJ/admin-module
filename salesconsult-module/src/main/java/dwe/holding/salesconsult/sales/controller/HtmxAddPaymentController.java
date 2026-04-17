package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.service.CustomerFinancialInfo;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.PaymentService;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Controller
@RequestMapping("/sales")
public class HtmxAddPaymentController {
    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final CustomerRepository customerRepository;
    private final CustomerFinancialInfo customerFinancialInfo;
    private final FinancialServiceInterface financialService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @GetMapping("/visit/{visitId}/payment")
    String getPaymentForModal(@PathVariable Long visitId, Model model) {
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        Appointment app = visit.getAppointment();
        LocalMemberPreferences localMemberPref = objectMapper.readValue(AutorisationUtils.getCurrentLocalMemberJsonPreferences(), LocalMemberPreferences.class);
        model.addAttribute("appointment", app);
        model.addAttribute("payMethodsList", PaymentMethodEnum.getWebList());
        model.addAttribute("customerId", visit.getPet().getCustomer().getId());
        model.addAttribute("payment",
                Payment.builder()
                        .method(localMemberPref.getPaymentMethod())
                        .paymentDate(LocalDate.now())
                        .amount(
                                app.getVisits().stream().map(Visit::getTotalAmountIncTax).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                        )
                        .localMemberId(visit.getAppointment().getLocalMemberId())
                        .build());

        return "sales-module/payment/htmx/paymentmodal";
    }

    @PostMapping("/customer/{customerId}/appointment/{appointmentId}/payment")
    String savePayment(@PathVariable Long customerId, @PathVariable Long appointmentId, PaymentForm paymentForm, HttpServletResponse response, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        // TODO validate if state is ok to add payment
        paymentService.addPayment(
                Payment.builder()
                        .paymentDate(paymentForm.getPaymentDate())
                        .method(paymentForm.getMethod())
                        .amount(paymentForm.getAmount())
                        .comments(paymentForm.getComments())
                        .referenceNumber(paymentForm.getReferenceNumber())
                        .customer(customer)
                        .memberId(AutorisationUtils.getCurrentUserMid())
                        .localMemberId(AutorisationUtils.getCurrentUserMlid())
                        .build(),
                paymentForm.getAddToVisit()
        );
        customerFinancialInfo.updateCustomerAndFinancialInfo(model, customer);

        response.setHeader("HX-Trigger", "closeModal");
        return "sales-module/fragments/htmx/updatebalancelastpayment";
    }

    record PetRec(Long petId, String petName, BigDecimal amount) {
    }

    @Getter
    @Setter
    static
    class PaymentForm extends Payment {
        private List<Long> addToVisit;
        private Long localMemberId;
    }

}