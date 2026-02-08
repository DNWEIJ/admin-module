package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.repository.VisitListProjection;
import dwe.holding.salesconsult.consult.repository.dsl.AppointmentListDsl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class ListVisitController {

    private final AppointmentListDsl appointmentListDsl;


    @GetMapping("/visit/list")
    String listAppointments(Model model, AppointmentListForm form, HttpServletRequest request) {
        if (form.from == null || form.includeTill() == null) {
            form = new AppointmentListForm(LocalDate.now().minusDays(1),
                    LocalDate.now(),
                    AutorisationUtils.getCurrentUserMlid(), false);
        }
        if (form.addMoney == null || !form.addMoney) {
            model.addAttribute("appointments",
                    appointmentListDsl.findVisits(
                            AutorisationUtils.getCurrentUserMid(), form.localMemberId, form.from, form.includeTill
                    )
            );
        } else {
            List<VisitListProjection> list = appointmentListDsl.findVisits(
                    AutorisationUtils.getCurrentUserMid(), form.localMemberId, form.from, form.includeTill
            );
            record AmountTotals(BigDecimal total, BigDecimal paid) {}

            AmountTotals totals = list.stream()
                    .map(v -> new AmountTotals(
                            v.totalAmount == null ? BigDecimal.ZERO :  v.totalAmount,
                            v.paidAmount == null ? BigDecimal.ZERO :  v.paidAmount
                    ))
                    .reduce(
                            new AmountTotals(BigDecimal.ZERO,BigDecimal.ZERO),
                            (a, b) -> new AmountTotals(
                                    a.total().add(b.total()),
                                    a.paid().add(b.paid())
                            )
                    );
            model
                    .addAttribute("appointments", list)
                    .addAttribute("visitsTotalAmount", totals.total)
                    .addAttribute("visitPaidAmount", totals.paid)
            ;
        }
        model
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("form", form);
        return "consult-module/visit/appointmentlist";
    }

    public record AppointmentListForm(
            LocalDate from, LocalDate includeTill, Long localMemberId, Boolean addMoney) {
    }
}
