package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Locale;

@RequestMapping("/report")
@AllArgsConstructor
@Controller
public class PaymentsReportController {

    private final MessageSource messageSource;

    @GetMapping("/payment")
    String payments(Model model, Locale locale, PaymentsForm form) {
        if (form.from == null || form.includeTill() == null) {
            form = new PaymentsForm(LocalDate.now().minusDays(1),
                    LocalDate.now(), AutorisationUtils.getCurrentUserMlid(), PaymentQueryEnum.PAYMENT_LISTING);
        }

        model
                .addAttribute("form", form)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList()
                        .add(new PresentationElement(0L, messageSource.getMessage("label.yesno.donotcare", null, locale))))
                .addAttribute("paymentList", PaymentQueryEnum.getWebList())
        ;
        return "reporting-module/payments";
    }


    public record PaymentsForm(LocalDate from, LocalDate includeTill, Long localMemberId, PaymentQueryEnum paymentQuery) {
    }

    public enum PaymentQueryEnum {
        PAYMENT_LISTING("label.paymentquery.listing"),
        UNDER_PAID("label.paymentquery.under"),
        OVER_PAID("label.paymentquery.over");

        private final String label;

        PaymentQueryEnum(String label) {
            this.label = label;
        }

        public static java.util.List<PaymentQueryEnum> getWebList() {
            return java.util.Arrays.stream(PaymentQueryEnum.values()).toList();
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
}
