package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.salesconsult.consult.model.PaymentVisit;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentService {
    private final VisitRepository visitRepository;
    private final PaymentRepository paymentRepository;
    private final FinancialServiceInterface financialService;

    @Transactional
    public void addPayment(Payment payment, List<Long> visitIds) {
        Payment savedPayment = paymentRepository.save(payment);

        List<Visit> visits = visitRepository.findByMemberIdAndIdIn(AutorisationUtils.getCurrentUserMid(), visitIds);

        Set<PaymentVisit> paymentVisits = visits.stream()
                .map(visit -> {
                    PaymentVisit pv = new PaymentVisit(savedPayment, visit);
                    visit.getPaymentVisits().add(pv);
                    return pv;
                })
                .collect(Collectors.toSet());

        savedPayment.getPaymentVisits().addAll(paymentVisits);
        paymentRepository.save(savedPayment);
        visitRepository.saveAll(visits);
        financialService.updateCustomerBalanceAndVisitTotal(savedPayment.getCustomer().getId(), 0L);
    }

    @Transactional
    public Payment addVisitPaymentToPayment(Payment payment, Visit visit) {
        payment.getPaymentVisits().add(PaymentVisit.builder().payment(payment).visit(visit).build());
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment deleteVisitPaymentFromPayment(Payment payment, Visit visit) {
        payment.getPaymentVisits()
                .removeIf(pv -> pv.getVisit().equals(visit));
        return paymentRepository.save(payment);
    }

}
