package dwe.holding.customer.client.controller;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Payment;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.PaymentRepository;
import dwe.holding.generic.shared.model.type.PaymentMethodEnum;
import dwe.holding.generic.shared.model.type.YesNoEnum;
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
@RequestMapping(path = "/customer")
@Slf4j
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ValidateCustomer validateCustomer;

    public PaymentController(PaymentRepository paymentRepository, CustomerRepository customerRepository, ValidateCustomer validateCustomer) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.validateCustomer = validateCustomer;
    }

    @PostMapping("/customer/{customerId}/payment")
    String newRecord(@PathVariable Long customerId, Payment paymentForm, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
//     TODO when AutorisationUtls is working
//        if (!customerId.equals(AutorisationUtils.getCustomerinfo().customerId())) {
//            return "redirect:/customer";
//  todo  add message       }

        if (paymentForm.isNew()) {
            Customer customer = customerRepository.findById(customerId).get();
            if (!customer.getMemberId().equals(77L)) { // TODO autorisationUtils.
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
                            .build()
            );
            customer.getPayments().add(savedPayment);
            customerRepository.save(customer);
            redirect.addFlashAttribute("message","label.saved" );
            return "redirect:/customer/customer/" + customer.getId() + "/payments";
        } else {
            Payment payment = paymentRepository.findById(paymentForm.getId()).get();
//            if ( // validate
//                    !payment.getCustomer().getId().equals(
//                            ((CustomerInformation) AutorisationUtils.getCustomerinfo().getInformation()).customerId())
//                            || !payment.getMemberId().equals(AutorisationUtils.getCurrentUserMid())
//            ) {
//                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
//                return "redirect:/customer/" + ((CustomerInformation) AutorisationUtils.getCustomerinfo().getInformation()).customerId() + "/payments";
//            }
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
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        model.addAttribute("payments", paymentRepository.findByCustomer_IdOrderByPaymentDateDesc(customerId));
        setModel(model, customerId);
        return "customer-module/payment/list";
    }

    @GetMapping("/customer/{customerId}/payment")
    String getRecord(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        model.addAttribute("payment",
                Payment.builder()
                        .method(PaymentMethodEnum.PIN)
                        .paymentDate(LocalDate.now())
                        .amount(0.0)
                        .build());
        setModel(model, customerId);
        return "customer-module/payment/action";
    }

    @GetMapping("/customer/{customerId}/payment/{paymentId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long paymentId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        Payment payment = paymentRepository.findById(paymentId).get();
        model.addAttribute("payment", payment.getCustomer().getId().equals(customerId) ? payment : new Payment());
        setModel(model, payment.getCustomer().getId());
        return "customer-module/payment/action";
    }

    void setModel(Model model, Long customerId) {
        model.addAttribute("activeMenu", "payments");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("payMethodsList", PaymentMethodEnum.getWebList());
        model.addAttribute("customerId", customerId);
    }
}