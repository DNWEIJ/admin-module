package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/report")
@AllArgsConstructor
@Controller
public class PrintController {

    private final CustomerService customerService;
    private final VisitRepository visitRepository;
    private final ReminderRepository reminderRepository;
    private final FinancialServiceInterface financialService;

    @PostMapping("/customer/{customerId}/print/{visitId}")
    String printInvoiceReceipt(@PathVariable Long visitId, @PathVariable Long customerId, Boolean docTypeSwitch, Boolean quantitySwitch, Model model) {
        // docTypeSwitch :: false -> invoice :: true -> receipt
        // quantitySwitch :: false -> single :: true -> multi
        if (docTypeSwitch == null) docTypeSwitch = Boolean.FALSE;
        if (quantitySwitch == null) quantitySwitch = Boolean.FALSE;

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();


        Map<Long, PetTotals> totalsPerPet = visit.getAppointment().getLineItems().stream()
                .collect(Collectors.toMap(
                        lineItem -> lineItem.getPet().getId(),
                        li -> new PetTotals(
                                li.getTotalIncTax(),
                                li.getTaxPortionOfProcessingFeeService(),
                                li.getTaxPortionOfProduct()
                        ),
                        (a, b) -> new PetTotals(
                                a.amount().add(b.amount()),
                                a.taxServiceAmount().add(b.taxServiceAmount()),
                                a.taxProductAmount().add(b.taxProductAmount())
                        )
                ));

        model
                .addAttribute("totalsPerPet", totalsPerPet)
                .addAttribute("invoiceTotals", totalsPerPet.values().stream()
                        .reduce(new PetTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                                (a, b) -> new PetTotals(
                                        a.amount().add(b.amount()),
                                        a.taxServiceAmount().add(b.taxServiceAmount()),
                                        a.taxProductAmount().add(b.taxProductAmount())
                                ))
                )
                .addAttribute("printDate", LocalDate.now().toString())
                .addAttribute("customer", customer)
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("memberLocal", AutorisationUtils.getFullLocalMemberMap().get(visit.getAppointment().getLocalMemberId()))
                .addAttribute("reminders", reminderRepository.findTop5ByPet_idInAndMemberIdAndDueDateGreaterThanOrderByDueDate(
                        visit.getAppointment().getVisits().stream().map(v -> v.getPet().getId()).toList()
                        , AutorisationUtils.getCurrentUserMid()
                        , LocalDate.now())
                )
                .addAttribute("balanceInfo", financialService.getCustomerBalance(customer.id()))
                .addAttribute("lastPaymentDate", financialService.getLastestPaymentDate(customer.id()))
                .addAttribute("lastPaymentAmount", financialService.getLastestPaymentAmount(customer.id()))
        ;
        if (docTypeSwitch) {
            model.addAttribute("printType", "receipt");
        } else {
            model.addAttribute("printType", "invoice");
        }
        if (quantitySwitch) {
            model.addAttribute("quantityType", "all");
        } else {
            model.addAttribute("quantityType", "single");
        }
        return "reporting-module/print/invoice";
    }

    public record PetTotals(
            BigDecimal amount,
            BigDecimal taxServiceAmount,
            BigDecimal taxProductAmount
    ) {
    }
}
