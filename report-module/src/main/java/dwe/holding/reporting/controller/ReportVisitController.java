package dwe.holding.reporting.controller;


import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.reporting.repository.dsl.EntityListDsls;
import dwe.holding.reporting.repository.projection.VisitListProjection;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Controller
@RequestMapping("/report")
@Slf4j
public class ReportVisitController {

    private final EntityListDsls entityListDsls;

    @GetMapping("/visits")
    String listAppointments(Model model, AppointmentListForm form) {
        if (form.getShowInsurance() == null) form.setShowInsurance(false);
        if (form.getShowAmountDiff() == null) form.setShowAmountDiff(false);
        if (form.getShowInvoice() == null) form.setShowInvoice(false);
        if (form.getShowPetname() == null) form.setShowPetname(false);
        if (form.getShowVetroom() == null) form.setShowVetroom(false);

        if (form.from == null || form.getIncludeTill() == null) {
            form = new AppointmentListForm(LocalDate.now().minusDays(1),
                    LocalDate.now(), 0L, "doNotCare", true, true, true, true, false
            );
        }

        List<VisitListProjection> list = entityListDsls.findVisits(AutorisationUtils.getCurrentUserMid(), form.localMemberId, form.from, form.includeTill);
        list.addAll(entityListDsls.findPaymentsNoVisit(AutorisationUtils.getCurrentUserMid(), form.localMemberId, form.from, form.includeTill));
        list = list.stream().sorted(Comparator.comparing(VisitListProjection::getVisitDateTime).thenComparing(VisitListProjection::getLastName)).toList();

        // todo push into query
        if (!form.invoiceSend.equals("doNotCare")) {
            YesNoEnum filterOn = (form.invoiceSend.equals("Yes") ? YesNoEnum.Yes : YesNoEnum.No);
            list = list.stream().filter(a -> a.sentToInsurance.equals(filterOn)).toList();
        }

        if (form.showAmountDiff.booleanValue()){
            list = list.stream().filter(rec ->
                    (rec.totalAmountIncTax.subtract(rec.paidAmount).compareTo(BigDecimal.ZERO)  != 0)
            ).toList();
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
        return "reporting-module/reporting/appointmentlist";
    }



    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class AppointmentListForm {
        private LocalDate from;
        private LocalDate includeTill;
        private Long localMemberId;
        private String invoiceSend;
        private Boolean showPetname;
        private Boolean showVetroom;
        private Boolean showInsurance;
        private Boolean showInvoice;
        private Boolean showAmountDiff;
    }

    public record AmountTotals(BigDecimal total, BigDecimal paid) {
    }

    public static AmountTotals applyFullPaymentDistributionToVisitAndCreateTotals(List<VisitListProjection> list) {

        Long currentAppointmentId = null;
        BigDecimal remainingPayment = BigDecimal.ZERO;
        BigDecimal totalSum = BigDecimal.ZERO;
        BigDecimal paidSum = BigDecimal.ZERO;

        for (VisitListProjection record : list) {

            // New appointment group → initialize once
            if (!Objects.equals(currentAppointmentId, record.appointmentId)) {
                currentAppointmentId = record.appointmentId;
                remainingPayment = record.paidAmount == null ? BigDecimal.ZERO : record.paidAmount;
            }
            BigDecimal applied = remainingPayment.min(record.totalAmountIncTax);
            remainingPayment = remainingPayment.subtract(applied);

            totalSum = totalSum.add(record.totalAmountIncTax);
            paidSum = paidSum.add(applied);
        }
        return new AmountTotals(totalSum, paidSum);
    }
}
