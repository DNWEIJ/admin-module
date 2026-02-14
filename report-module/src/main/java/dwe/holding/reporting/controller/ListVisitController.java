package dwe.holding.reporting.controller;


import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.reporting.repository.dsl.AppointmentListDsl;
import dwe.holding.reporting.repository.projection.VisitListProjection;
import dwe.holding.shared.model.type.YesNoEnum;

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
import java.util.Objects;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/report")
@Slf4j
public class ListVisitController {

    private final AppointmentListDsl appointmentListDsl;

    @GetMapping("/visit/list")
    String listAppointments(Model model, AppointmentListForm form, HttpServletRequest request) {
        if (form.from == null || form.includeTill() == null) {
            form = new AppointmentListForm(LocalDate.now().minusDays(1),
                    LocalDate.now(),
                    AutorisationUtils.getCurrentUserMlid(), "doNotCare");
        }

        List<VisitListProjection> list = appointmentListDsl.findVisits(
                AutorisationUtils.getCurrentUserMid(), form.localMemberId, form.from, form.includeTill
        );

        if (!form.invoiceSend.equals("doNotCare")) {
            YesNoEnum filterOn = (form.invoiceSend.equals("Yes") ? YesNoEnum.Yes : YesNoEnum.No);
            list = list.stream().filter(a -> a.sentToInsurance.equals(filterOn)).toList();
        }
        AmountTotals totals = applyFullPaymentDistributionToVisitAndCreateTotals(list);

        model
                .addAttribute("appointments", list)
                .addAttribute("visitsTotalAmount", totals.total)
                .addAttribute("visitPaidAmount", totals.paid)
        ;
        model
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("ynvaluesList", YesNoEnum.getWebListDoNotCare())
                .addAttribute("form", form);
        return "reporting-module/appointmentlist";
    }


    public record AppointmentListForm(
            LocalDate from, LocalDate includeTill, Long localMemberId, String invoiceSend) {
    }

    public record AmountTotals(BigDecimal total, BigDecimal paid) {
    }

    public static AmountTotals applyFullPaymentDistributionToVisitAndCreateTotals(List<VisitListProjection> list) {

        Long currentAppointmentId = null;
        BigDecimal remainingPayment = BigDecimal.ZERO;
        BigDecimal totalSum = BigDecimal.ZERO;
        BigDecimal paidSum = BigDecimal.ZERO;

        for (VisitListProjection row : list) {

            BigDecimal lineTotal = row.totalAmount == null ? BigDecimal.ZERO : row.totalAmount;

            // New appointment group → initialize once
            if (!Objects.equals(currentAppointmentId, row.appointmentId)) {
                currentAppointmentId = row.appointmentId;
                remainingPayment = row.paidAmount == null ? BigDecimal.ZERO : row.paidAmount;
            }
            BigDecimal applied = remainingPayment.min(lineTotal);
            remainingPayment = remainingPayment.subtract(applied);

//            row.setPaidAmount(applied);

            totalSum = totalSum.add(row.totalAmount);
            paidSum = paidSum.add(applied);

        }
        return new AmountTotals(totalSum, paidSum);
    }
}
