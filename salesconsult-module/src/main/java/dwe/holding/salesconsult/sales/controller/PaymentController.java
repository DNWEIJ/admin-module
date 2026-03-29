package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.PaymentService;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/sales")
@Slf4j
@AllArgsConstructor
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final VisitRepository visitRepository;
    private final PaymentService paymentService;

    @PostMapping("/customer/{customerId}/payment")
    String newOrUpdateRecord(@PathVariable Long customerId, Payment paymentForm, RedirectAttributes redirect) {

        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        if (paymentForm.isNew()) {
            if (!customer.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
                return "redirect:/customer/customer";
            }

            Payment savedPayment = paymentRepository.save(
                    Payment.builder()
                            .paymentDate(paymentForm.getPaymentDate())
                            .method(paymentForm.getMethod())
                            .amount(paymentForm.getAmount())
                            .comments(paymentForm.getComments())
                            .referenceNumber(paymentForm.getReferenceNumber())
                            .customer(customer)
                            .memberId(AutorisationUtils.getCurrentUserMid())
                            .localMemberId(AutorisationUtils.getCurrentUserMlid())
                            .build()
            );
            customerRepository.save(customer);
            redirect.addFlashAttribute("message", "label.saved");
            return "redirect:/customer/customer/" + customer.getId() + "/payments";
        } else {
            Payment payment = paymentRepository.findById(paymentForm.getId()).orElseThrow();
            if (!payment.getCustomer().getId().equals(customer.getId())) {
                log.error("Staff {} tried to edit payment {} (customer {}) of another customer {}", AutorisationUtils.getCurrentUserAccount(), payment.getId(), payment.getCustomer().getId(), customer.getId());
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
                return "redirect:/vmas/index";
            }
            payment.setPaymentDate(paymentForm.getPaymentDate());
            payment.setMethod(paymentForm.getMethod());
            payment.setAmount(paymentForm.getAmount());
            payment.setComments(paymentForm.getComments());
            payment.setReferenceNumber(paymentForm.getReferenceNumber());

            paymentRepository.save(payment);
            return "redirect:/customer/customer/" + payment.getCustomer().getId() + "/payments";
        }
    }

    @GetMapping("/customer/{customerId}/payments")
    String list(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model
                .addAttribute("payments", paymentRepository.findByCustomer_IdOrderByPaymentDateDesc(customer.getId()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap());
        setModel(model, customerId);
        return "sales-module/payment/list";
    }

    @GetMapping("/customer/{customerId}/payment")
    public String getNewPaymentRecord(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("payment",
                Payment.builder()
                        .method(PaymentMethodEnum.PIN)
                        .paymentDate(LocalDate.now())
                        .amount(BigDecimal.ZERO)
                        .build());
        setModel(model, customer.getId());
        return "sales-module/payment/action";
    }

    @GetMapping("/customer/{customerId}/payment/{paymentId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long paymentId, Model model, RedirectAttributes redirect) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        Payment payment = paymentRepository.findById(paymentId).get();
        model.addAttribute("payment", payment.getCustomer().getId().equals(customerId) ? payment : new Payment());
        setModel(model, payment.getCustomer().getId());
        return "sales-module/payment/action";
    }

    @DeleteMapping("/payment/{paymentId}/visit/{visitId}")
    String deleteHtmxVisitPaymentLink(@PathVariable Long paymentId, @PathVariable Long visitId, Model model) {
        Payment payment = paymentRepository.findById(paymentId).get();
        Optional<Visit> optionalVisit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId);
        model.addAttribute("payment", paymentService.deleteVisitPaymentFromPayment(payment, optionalVisit.get()));
        return "sales-module/payment/paymentform::paymentVisitConnection";
    }

    @PostMapping("/payment/{paymentId}/add")
    String addHtmxVisitPaymentLink(@PathVariable Long paymentId, Long newVisitId, Model model) {
        Payment payment = paymentRepository.findById(paymentId).get();
        Optional<Visit> optionalVisit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(),newVisitId);
        if (optionalVisit.isPresent()) {
            model.addAttribute("payment", paymentService.addVisitPaymentToPayment(payment, optionalVisit.get()));
            return "sales-module/payment/paymentform::paymentVisitConnection";
        } else {
            model.addAttribute("error", true);
            model.addAttribute("payment", payment);
            return "sales-module/payment/paymentform::paymentVisitConnection";
        }
    }

    void setModel(Model model, Long customerId) {
        model.addAttribute("activeMenu", "payments");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("payMethodsList", PaymentMethodEnum.getWebList());
        model.addAttribute("customerId", customerId);
    }
}