package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.reporting.PaymentListTypeEnum;
import dwe.holding.reporting.repository.dsl.EntityListDsls;
import dwe.holding.reporting.repository.projection.PaymentListProjection;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/report")
@AllArgsConstructor
@Controller
public class ReportPaymentsController {
    private final EntityListDsls entityListDsls;

    @GetMapping("/payment")
    String payments(Model model, PaymentsForm form) {
        if (form.from == null || form.includeTill() == null) {
            form = new PaymentsForm(LocalDate.now().minusDays(1),
                    LocalDate.now(), AutorisationUtils.getCurrentUserMlid(), PaymentListTypeEnum.STANDARD);
        }
        model
                .addAttribute("form", form)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("paymentListType", PaymentListTypeEnum.getWebList())
                .addAttribute("payments", getList(form))
        ;
        return "reporting-module/payments";
    }

    List<PaymentListProjection> getList(PaymentsForm form) {
        if (PaymentListTypeEnum.STANDARD.equals(form.paymentListType)) {
         return entityListDsls.findPaymentsWithBalance(AutorisationUtils.getCurrentUserMid(),form.localMemberId(), form.from(), form.includeTill());
        }
        return null;
    }
    public record PaymentsForm(LocalDate from, LocalDate includeTill, Long localMemberId, PaymentListTypeEnum paymentListType) {
    }

    public void sqlcall() {
        String call =
                """
                        
                        CREATE PROCEDURE `PaymentOverPaid`(In i_mlid bigint(20),In i_mid bigint(20),In flag int)
                        BEGIN
                        
                        CREATE TEMPORARY TABLE IF NOT EXISTS TMP (ownerName varchar (50),balanceValue double);
                        
                               if(flag = 0)  then
                        Insert into TMP
                        select FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial), FuncOwnerBalanceAtMLID(customer_id,null,i_mlid)
                        from customer AS c where c.mid = i_mid and FuncOwnerBalanceAtMLID(customer_id,null,i_mlid) > 0;
                                else
                        Insert into TMP
                        select FuncFormatOwnerName(c.LastName,c.FirstName,c.Surname,c.MiddleInitial),
                        FuncOwnerBalanceAtMLID(customer_id,null,i_mlid)
                        from customer AS c where c.mid = i_mid and FuncOwnerBalanceAtMLID(customer_id,null,i_mlid) < 0;
                        end if;
                        
                        select distinct ownerName,balanceValue FROM TMP order by ownerName;
                        
                        DROP TEMPORARY table IF EXISTS TMP;
                        
                        END $$
                        """;
    }
}
