package dwe.holding.salesconsult.sales.Service;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.service.intrfce.FinancialServiceInterface;
import dwe.holding.salesconsult.consult.model.PaymentVisit;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.model.Payment;
import dwe.holding.salesconsult.sales.repository.PaymentListProjection;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import dwe.holding.salesconsult.sales.repository.projection.PaymentVisitDTO;
import dwe.holding.salesconsult.sales.repository.projection.VisitDto;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentService {
    private final VisitRepository visitRepository;
    private final PaymentRepository paymentRepository;
    private final FinancialServiceInterface financialService;
    private final CustomerRepository customerRepository;

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

    public List<PaymentListProjection> findPaymentsWithBalance(Long memberId, Long localMemberId, LocalDate from, LocalDate localDate) {
        List<Payment> payments = paymentRepository.findPaymentsWithBalance(memberId, localMemberId, from, localDate);

        List<Long> paymentIds = payments.stream()
                .map(Payment::getId)
                .toList();

        Map<Long, List<PaymentListProjection.VisitIdAppointmentId>> visitsByPayment =
                paymentRepository.findVisitAndAppointmentId(paymentIds).stream()
                        .collect(Collectors.groupingBy(
                                row -> (Long) row[0],  // payment ID
                                Collectors.mapping(
                                        row -> new PaymentListProjection.VisitIdAppointmentId((Long) row[1], (Long) row[2], (YesNoEnum) row[3]),  // visit.id, appointment.id, OTC
                                        Collectors.toList()
                                )
                        ));


        return payments.stream()
                .map(p -> new PaymentListProjection(
                        p.getId(),
                        p.getPaymentDate(),
                        p.getLocalMemberId(),
                        p.getReferenceNumber(),
                        p.getMethod(),
                        p.getAmount(),
                        visitsByPayment.get(p.getId()),
                        p.getCustomer().getId(),
                        p.getCustomer().getLastName(),
                        p.getCustomer().getFirstName(),
                        p.getCustomer().getSurName(),
                        p.getCustomer().getMiddleInitial(),
                        p.getCustomer().getBalance(),
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                ))
                .toList();
    }

    public List<PaymentListProjection> findCustomerWithPositiveBalance() {
        return map(customerRepository.findByBalanceGreaterThan(BigDecimal.ZERO));
    }

    public List<PaymentListProjection> findCustomerWithNegativeBalance() {
        return map(customerRepository.findByBalanceLessThan(BigDecimal.ZERO));
    }

    private List<PaymentListProjection> map(List<Customer> customerList) {
        return customerList.stream()
                .map(c -> new PaymentListProjection(
                        null, null, null, null, null, null, null,
                        c.getId(),
                        c.getLastName(),
                        c.getFirstName(),
                        c.getSurName(),
                        c.getMiddleInitial(),
                        c.getBalance(),
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                ))
                .toList();
    }

    public List<PaymentListProjection> findCustomerWithNegativeBalanceAR() {
        List<Customer> customers = customerRepository.findByBalanceLessThan(BigDecimal.ZERO);
        List<Long> customerIds = customers.stream().map(Customer::getId).toList();
        // customerIds = List.of(26722L);
        List<VisitDto> visits = visitRepository.findByPet_CustomerIdInOrderByPet_CustomerIdAscAppointment_visitDateTimeAsc(customerIds);
        List<PaymentVisitDTO> payments = paymentRepository.findPaymentsForCustomers(customerIds);

        Map<Long, List<VisitDto>> visitsByCustomer = visits.stream().collect(Collectors.groupingBy(VisitDto::getCustomerId));
        Map<Long, List<PaymentVisitDTO>> paymentsByCustomers = payments.stream().collect(Collectors.groupingBy(PaymentVisitDTO::customerId));

        List ar = new ArrayList<PaymentListProjection>();
        for (Customer customer : customers) {
            List<PaymentVisitDTO> paymentsForCustomer = paymentsByCustomers.get(customer.getId());
            List<VisitDto> visitsForCustomer = visitsByCustomer.get(customer.getId());
            ar.add(createAR(customer, visitsForCustomer == null ? List.of() : visitsForCustomer, paymentsForCustomer == null ? List.of() : paymentsForCustomer));
        }
        return ar;
    }

    public PaymentListProjection createAR(Customer customer, List<VisitDto> visits, List<PaymentVisitDTO> payments) {

        record Event(LocalDate date, BigDecimal amount) {
        }

        LocalDate today = LocalDate.now();
        List<Event> events = new ArrayList<>();
        BigDecimal b0_30 = BigDecimal.ZERO, b31_60 = BigDecimal.ZERO, b61_90 = BigDecimal.ZERO, b90p = BigDecimal.ZERO;

        for (VisitDto v : visits) events.add(new Event(v.getVisitDateTime().toLocalDate(), v.getTotalAmountIncTax()));
        for (PaymentVisitDTO p : payments) events.add(new Event(p.paymentDate(), p.paymentAmount().negate()));

        // sort all events in time
        events.sort(Comparator.comparing(Event::date));

        // We need invoice aging, so we track open invoices separately
        List<VisitDto> sortedVisits = new ArrayList<>(visits);
        sortedVisits.sort(Comparator.comparing(VisitDto::getVisitDateTime));

        BigDecimal[] open = new BigDecimal[sortedVisits.size()];
        for (int i = 0; i < sortedVisits.size(); i++)
            open[i] = sortedVisits.get(i).getTotalAmountIncTax();

        int ptr = 0;

        for (PaymentVisitDTO p : payments) {

            BigDecimal pay = p.paymentAmount();
            while (pay.compareTo(BigDecimal.ZERO) > 0 && ptr < sortedVisits.size()) {
                BigDecimal cur = open[ptr];
                if (cur.compareTo(BigDecimal.ZERO) == 0) {
                    ptr++;
                    continue;
                }

                BigDecimal applied = pay.min(cur);
                open[ptr] = cur.subtract(applied);
                pay = pay.subtract(applied);

                if (open[ptr].compareTo(BigDecimal.ZERO) == 0)
                    ptr++;
            }
        }

        for (int i = 0; i < sortedVisits.size(); i++) {

            BigDecimal openAmt = open[i];
            if (openAmt.compareTo(BigDecimal.ZERO) == 0) continue;

            long days = ChronoUnit.DAYS.between(
                    sortedVisits.get(i).getVisitDateTime().toLocalDate(),
                    today
            );

            if (days <= 30) b0_30 = b0_30.add(openAmt);
            else if (days <= 60) b31_60 = b31_60.add(openAmt);
            else if (days <= 90) b61_90 = b61_90.add(openAmt);
            else b90p = b90p.add(openAmt);
        }

        return new PaymentListProjection(
                null, null, null, null, null, null, null,
                customer.getId(),
                customer.getLastName(),
                customer.getFirstName(),
                customer.getSurName(),
                customer.getMiddleInitial(),
                customer.getBalance(),
                b0_30, b31_60, b61_90, b90p
        );
    }


//    PaymentListProjection createAR(Customer customer, List<VisitDto> visitsByCustomer, List<PaymentVisitDTO> paymentsByCustomer) {
//
//        BigDecimal bucket0_30 = BigDecimal.ZERO;
//        BigDecimal bucket31_60 = BigDecimal.ZERO;
//        BigDecimal bucket61_90 = BigDecimal.ZERO;
//        BigDecimal bucket_greather_90 = BigDecimal.ZERO;
//        LocalDate localDateNow = LocalDate.now();
//
//        Map<Long, VisitDto> visitsByVisitId = visitsByCustomer.stream().collect(Collectors.toMap(VisitDto::getVisitId, Function.identity()));
//        Map<LocalDate, List<VisitDto>> visitsByDate = visitsByCustomer.stream().collect(Collectors.groupingBy(v -> v.getVisitDateTime().toLocalDate()));
//
//        if (!paymentsByCustomer.isEmpty())
//            paymentsByCustomer.sort(
//                    Comparator.comparing(PaymentVisitDTO::paymentVisitId, Comparator.nullsLast(Comparator.reverseOrder())).reversed()
//            );
//        Long paymentId = 0L;
//
//        BigDecimal amountPaid = BigDecimal.ZERO;
//        // Nowadays, most payments are added via the actual visit and this is the way it should be.....
//        // however in history, payments have been added without a link to a visit. This needs to be handled separately. If the paymentVisitId == null it is generic payment on date (possibly)
//        for (PaymentVisitDTO payment : paymentsByCustomer) {
//            if (!paymentId.equals(payment.paymentId())) {
//                paymentId = payment.paymentId();
//                amountPaid = payment.paymentAmount();
//            }
//            // first: it can be that this payment is not connected
//            if (payment.visitId() == null) {
//                // find a visit on this date
//                List<VisitDto> list = visitsByDate.get(payment.paymentDate());
//                if (!list.isEmpty()) {
//                    // TODO
////                    list.forEach(visit -> {
////                        if (patment. visit.getTotalAmountIncTax())
////                    });
////
////                    };
//                }
//            }
//
//            VisitDto visit = visitsByVisitId.get(payment.visitId());
//            if (amountPaid.compareTo(visit.getTotalAmountIncTax()) >= 0) {
//                amountPaid = amountPaid.subtract(visit.getTotalAmountIncTax());
//            } else {
//                // paid amount doesn't cover the costs...
//                long days = ChronoUnit.DAYS.between(visit.getVisitDateTime().toLocalDate(), localDateNow);
//                if (days <= 30) {
//                    bucket0_30 = bucket0_30.add(visit.getTotalAmountIncTax().subtract(amountPaid));
//                    amountPaid = BigDecimal.ZERO;
//                } else if (days <= 60) {
//                    bucket31_60 = bucket31_60.add(visit.getTotalAmountIncTax().subtract(amountPaid));
//                    amountPaid = BigDecimal.ZERO;
//                } else if (days <= 90) {
//                    bucket61_90 = bucket61_90.add(visit.getTotalAmountIncTax().subtract(amountPaid));
//                    amountPaid = BigDecimal.ZERO;
//                } else {
//                    bucket_greather_90 = bucket_greather_90.add(visit.getTotalAmountIncTax().subtract(amountPaid));
//                    amountPaid = BigDecimal.ZERO;
//                }
//            }
//        }
//
//        return new PaymentListProjection(null, null, null, null, null, null, null,
//                customer.getId(), customer.getLastName(), customer.getFirstName(), customer.getSurName(), customer.getMiddleInitial(), customer.getBalance(),
//                // ar.firstMonth(), ar.firstMonth(),ar.firstMonth(),ar.firstMonth()
//                bucket0_30, bucket31_60, bucket_greather_90, bucket_greather_90
//        );

}
