package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.reporting.PaymentListTypeEnum;
import dwe.holding.reporting.repository.dsl.EntityListDsls;
import dwe.holding.salesconsult.sales.repository.PaymentListProjection;
import dwe.holding.salesconsult.sales.Service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/report")
@AllArgsConstructor
@Controller
public class ReportPaymentsController {
    private final PaymentService paymentService;

    @GetMapping("/payment")
    String payments(Model model, PaymentsForm form, @RequestParam PaymentListTypeEnum paymentListType) {
        if (form.from == null || form.includeTill() == null) {
            form = new PaymentsForm(LocalDate.now().minusDays(1),
                    LocalDate.now(), AutorisationUtils.getCurrentUserMlid(), paymentListType);
        }
        model
                .addAttribute("form", form)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("localMembersDisplay", AutorisationUtils.getLocalMemberMap())
                .addAttribute("paymentListType", PaymentListTypeEnum.getWebList())
                .addAttribute("payments", getList(form))
        ;
        return "reporting-module/reporting/payments";
    }

    List<PaymentListProjection> getList(PaymentsForm form) {
        if (PaymentListTypeEnum.STANDARD.equals(form.paymentListType)) {
            return paymentService.findPaymentsWithBalance(AutorisationUtils.getCurrentUserMid(), form.localMemberId(), form.from(), form.includeTill());
        }
        if (PaymentListTypeEnum.OUT.equals(form.paymentListType))  {
            return paymentService.findCustomerWithNegativeBalance();
        }
        if (PaymentListTypeEnum.OVER.equals(form.paymentListType)) {
            return paymentService.findCustomerWithPositiveBalance();
        }
        if (PaymentListTypeEnum.AR.equals(form.paymentListType)) {
            return paymentService.findCustomerWithNegativeBalanceAR();
        }
        throw new RuntimeException("Invalid PaymentListType");
    }

    public record PaymentsForm(LocalDate from, LocalDate includeTill, Long localMemberId, PaymentListTypeEnum paymentListType) {
    }
}
