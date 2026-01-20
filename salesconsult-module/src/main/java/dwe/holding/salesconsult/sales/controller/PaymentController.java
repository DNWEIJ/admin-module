package dwe.holding.salesconsult.sales.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping(path = "/sales")
@Slf4j
@AllArgsConstructor
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

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
                            .localMemberId(AutorisationUtils.getCurrentUserMid())
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
    String list(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model
                .addAttribute("payments", paymentRepository.findByCustomer_IdOrderByPaymentDateDesc(customer.getId()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap());
        setModel(model, customerId);
        return "sales-module/payment/list";
    }

    @GetMapping("/customer/{customerId}/payment")
    public String getPaymentRecord(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("payment",
                Payment.builder()
                        .method(PaymentMethodEnum.PIN)
                        .paymentDate(LocalDate.now())
                        .amount(0.0)
                        .build());
        setModel(model, customerId);
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

    void setModel(Model model, Long customerId) {
        model.addAttribute("activeMenu", "payments");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("payMethodsList", PaymentMethodEnum.getWebList());
        model.addAttribute("customerId", customerId);
    }
}