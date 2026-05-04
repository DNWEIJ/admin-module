package dwe.holding.vmas.setup;

import dwe.holding.admin.authorisation.tenant.localmember.LocalMemberRepository;
import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.repository.LineItemRepository;
import dwe.holding.salesconsult.sales.repository.PaymentRepository;
import dwe.holding.salesconsult.sales.repository.projection.PaymentVisitDTO;
import dwe.holding.salesconsult.sales.repository.projection.VisitDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class UpdateDatabase {
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final LineItemRepository lineItemRepository;
    private final VisitRepository visitRepository;
    private final LocalMemberRepository localMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void processAllCustomerBalance() {
        log.info("Processing all customers starting at: {}", LocalDateTime.now());
        long customerRecs = customerRepository.countByBalanceIsNull();
        if (customerRecs != 0) {
            customerRepository.findAllIds()
                    .forEach(customerId ->
                            customerRepository.updateBalance(customerId, getCustomerBalance(customerId))
                    );
        }
        log.info("Processing all customers finish at: {}", LocalDateTime.now());
    }

    @Transactional
    public void processAllVisitsBalance() {
        log.info("Processing all visits starting at: {}", LocalDateTime.now());
        long recIds = visitRepository.countByTotalAmountIncTaxEquals(BigDecimal.ZERO);
        if (recIds != 0) {
            visitRepository.zeroAmountOnVisitMaintenanceMethod();
            visitRepository.updateAllTotalAmountsMaintenanceMethod();
        }
        log.info("Processing all visits finish at: {}", LocalDateTime.now());
    }

    @Transactional
    public void reduceTaxRecords() {
        localMemberRepository.findByMemberId(77L).forEach(local -> {
            List<LocalMemberTax> list = merge(local.getMemberLocalTaxs());
            local.getMemberLocalTaxs().clear();
            local.getMemberLocalTaxs().addAll(list);
            localMemberRepository.save(local);
        });
    }

    List<LocalMemberTax> merge(List<LocalMemberTax> input) {
        if (input.isEmpty()) return List.of();

        List<LocalMemberTax> sorted = input.stream()
                .sorted(Comparator.comparing(LocalMemberTax::getStartDate))
                .toList();

        List<LocalMemberTax> result = new ArrayList<>();

        LocalMemberTax current = sorted.get(0);

        for (int i = 1; i < sorted.size(); i++) {
            LocalMemberTax next = sorted.get(i);

            boolean sameTax = current.getTaxLow().compareTo(next.getTaxLow()) == 0 && current.getTaxHigh().compareTo(next.getTaxHigh()) == 0;

            boolean overlapsOrTouches = !next.getStartDate().isAfter(current.getEndDate().plusDays(1));

            if (sameTax && overlapsOrTouches) {
                // merge → extend end date
                current.setEndDate(current.getEndDate().isAfter(next.getEndDate()) ? current.getEndDate() : next.getEndDate());
            } else {
                result.add(current);
                current = next;
            }
        }

        result.add(current);
        return result;
    }

    BigDecimal getCustomerBalance(Long customerId) {
        BigDecimal amountPaid = paymentRepository.getSumAmountOfPayment(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal amountOutStanding = lineItemRepository.getSumAmountOfLineItem(customerId).setScale(6, RoundingMode.HALF_UP);
        BigDecimal result = amountPaid.subtract(amountOutStanding);
        return result.abs().compareTo(new BigDecimal("0.01")) < 0 ? BigDecimal.ZERO : result;
    }

    private record PaymentVisitIds(Long paymentId, Long visitId) {
    }

    @Transactional
    public void processConnectPaymentToVisitPassOne() {
        // using two pas system:

        // probably good to only do this if the balance is zero!!

        // exact match full payment per visit or full payment for all visits, per appointment.
        // Second pass: the list should be reduced considerably, and we now need to mach 'more or less' on paid and visits matching
//        https://localhost:8443/consult/visit/customer/66979/visits
        // Also see if we find consult with under paid (consult amouont > paid) then see if there is a payment without connection that fills the gap

        List<PaymentVisitIds> listIds = new ArrayList<>();

        log.info("Processing NOT connect payments starting at: {}", LocalDateTime.now());
        long start = System.currentTimeMillis();
        List<PaymentVisitDTO> notConnectedPayments = paymentRepository.findMigrationPaymentsNotLinkedForCustomers();
        long end = System.currentTimeMillis();

        log.info("Records found: " + notConnectedPayments.size() + " => Time taken: " + (end - start) + " ms (" + (end - start) / 1000.0 + " s)");
        notConnectedPayments.stream()
                .limit(10)
                .forEach(a -> log.info("paymentId: " + a.paymentId() + " " + a.paymentAmount() + " " + a.customerId()));

        start = System.currentTimeMillis();
        List<VisitDto> notConnectedVisits = visitRepository.findMigrationNotConnectedAndNotZeroAmount();
        end = System.currentTimeMillis();
        log.info("Record found: " + notConnectedVisits.size() + " => Time taken: " + (end - start) + " ms (" + (end - start) / 1000.0 + " s)");

        Map<Long, List<VisitDto>> visitsByCustomer = notConnectedVisits.stream().collect(Collectors.groupingBy(VisitDto::getCustomerId));
        Map<Long, List<PaymentVisitDTO>> paymentsByCustomers = notConnectedPayments.stream().collect(Collectors.groupingBy(PaymentVisitDTO::customerId));

        log.info("start processing of the linking");
        start = System.currentTimeMillis();

        paymentsByCustomers.forEach((customerId, payments) -> {
            List<VisitDto> visitPerCustomer = visitsByCustomer.get(customerId);
            if (visitPerCustomer == null || visitPerCustomer.isEmpty()) {
                log.error("Found no visits for customer: " + customerId + " but found payments #: " + payments.size());
            } else {
                Map<LocalDate, List<VisitDto>> groupedVisitOnDate = visitPerCustomer.stream().collect(Collectors.groupingBy(dto -> dto.getVisitDateTime().toLocalDate()));
                Map<LocalDate, List<PaymentVisitDTO>> groupedPaymentOnDate = payments.stream().collect(Collectors.groupingBy(dto -> dto.paymentDate()));

                for (PaymentVisitDTO payment : payments) {
                    List<VisitDto> visitOnDate = groupedVisitOnDate.get(payment.paymentDate());
                    if (visitOnDate == null || visitOnDate.isEmpty()) {
                        break;
                    }

                    // if both (visits/payments) have the same sum amount, connect them.
                    BigDecimal combinedVisitAmount = visitOnDate.stream().map(VisitDto::getTotalAmountIncTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal combinedPaymentAmount = payments.stream().map(PaymentVisitDTO::paymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (combinedPaymentAmount.compareTo(combinedVisitAmount) >= 0) {
                        for (PaymentVisitDTO pymnt : payments) {
                            visitOnDate.forEach(visit -> {
                                listIds.add(new PaymentVisitIds(pymnt.paymentId(), visit.getVisitId()));
                            });
                        }
                    } else {
                        // if there is only 1 visit, then the payment will cover this payment (diff = 0)
                        // OR
                        // it will cover this payment AND other visit of a different date (partially) diff > 0
                        // OR
                        // it will partially cover this visit (next day the rest is paid for example) diff < 0
                        //
                        // So we will always link the payment, unless the payment is zero
                        //
                        // We also register if the diff != 0 in order for the next payment to find out if it is a match
                        if (!findVisits(payment, visitOnDate, listIds, combinedVisitAmount)) {
                            // so not on the date, maybe payment has been done a couple of days later....max 7 days back search for visit to connect.
                            for (int days = 1; days < 8; days++) {
                                List<VisitDto> previousVisitOnDate = groupedVisitOnDate.get(payment.paymentDate().minusDays(days));
                                // ensure the visit is not already within the to-be-connected list
                                if (previousVisitOnDate != null && !previousVisitOnDate.isEmpty()) {
                                    for (VisitDto visit : previousVisitOnDate) {
                                        if (!listIds.stream()
                                                .anyMatch(p -> Objects.equals(p.visitId(), visit.getVisitId())
                                                )) {
                                            // if no match found?
                                            if (payment.paymentAmount().equals(visit.getTotalAmountIncTax())) {
                                                listIds.add(new PaymentVisitIds(payment.paymentId(), visit.getVisitId()));
                                                break;
                                            }
                                        }
                                    }
                                    ;
                                }
                            }
                        }
                    }
                }
            }
        });
        end = System.currentTimeMillis();
        log.info("Time taken for linking: " + (end - start) + " ms (" + (end - start) / 1000.0 + " s)");

        start = System.currentTimeMillis();
        bulkInsertPaymnetVisits(listIds);
        end = System.currentTimeMillis();
        log.info("Time taken for save to db: " + (end - start) + " ms (" + (end - start) / 1000.0 + " s)");
    }


    @Transactional
    public void processConnectPaymentToVisitPassTwo() {

        // First: find exact amount of payment on a consult that isn't connected yet.
        // Second: with a spread of a couple of days like plus or minus a week, consolidate payments: See if a payment is connected to a visit that is NOT fully paid;
        //           Match a paymnet to see if the amount matches, or all not connected payments match
        //           Use the customer balance (0.0) as guidance that there should not be a payent that doesn't have a link

        List<PaymentVisitIds> listIds = new ArrayList<>();
        List<PaymentVisitDTO> notConnectedPaymentsAmountZero = paymentRepository.findMigrationPaymentsNotLinkedForCustomers();
        List<VisitDto> notConnectedVisits = visitRepository.findMigrationNotConnected();

        Map<Long, List<VisitDto>> visitsByCustomer = notConnectedVisits.stream().collect(Collectors.groupingBy(VisitDto::getCustomerId));
        Map<Long, List<PaymentVisitDTO>> paymentsByCustomers = notConnectedPaymentsAmountZero.stream().collect(Collectors.groupingBy(PaymentVisitDTO::customerId));

        paymentsByCustomers.forEach((customerId, payments) -> {
            List<VisitDto> visitPerCustomer = visitsByCustomer.get(customerId);
            if (visitPerCustomer == null || visitPerCustomer.isEmpty()) {
                // no visits would be really strange, since there is a payment.
                // so what can be the case, the payment is a partial payment and should be connected to an already exising connected visit. So 1 visit two payments
                // 2 visits (2 pets) 2 payments (cash / pin)
                // we will get all visits, instead of not connected and check if we can have this payment connected.
                checkOnAllVisitsForMatch(customerId, payments, listIds);
                //log.error("Found no visits for customer: " + customerId + " but found payments #: " + payments.size());
            } else {
                Map<BigDecimal, List<VisitDto>> groupedVisitOnAmount = visitPerCustomer.stream().collect(Collectors.groupingBy(dto -> dto.getTotalAmountIncTax()));
                for (PaymentVisitDTO payment : payments) {
                    List<VisitDto> visitOnAmount = groupedVisitOnAmount.get(payment.paymentAmount());
                    if (visitOnAmount != null && !visitOnAmount.isEmpty()) {
                        listIds.add(new PaymentVisitIds(payment.paymentId(), visitOnAmount.get(0).getVisitId()));
                    }
                }
            }
        });
        bulkInsertPaymnetVisits(listIds);
    }


    private void checkOnAllVisitsForMatch(Long customerId, List<PaymentVisitDTO> payments, List<PaymentVisitIds> listIds) {
//        List<Visit> visits = visitRepository.findByPet_Customer_Id(customerId);
//        Map<LocalDate, List<VisitDto>> groupedVisitOnDate = visitPerCustomer.stream().collect(Collectors.groupingBy(dto -> dto.getVisitDateTime().toLocalDate()));
//
    }

    private static boolean findVisits(PaymentVisitDTO payment, List<VisitDto> visitOnDate, List<PaymentVisitIds> listIds, BigDecimal combinedVisitAmount) {
        if (visitOnDate.size() == 1) {
            if (payment.paymentAmount().compareTo(visitOnDate.get(0).getTotalAmountIncTax()) >= 0) {
                listIds.add(new PaymentVisitIds(payment.paymentId(), visitOnDate.get(0).getVisitId()));
                return true;
            }
        } else {
            // if the amount combined is equal to the payment, it is covered
            // if the amount combined is smaller than the payment, it is covered
            if (payment.paymentAmount().compareTo(combinedVisitAmount) >= 0) {
                visitOnDate.forEach(visitOnDateElement -> {
                    listIds.add(new PaymentVisitIds(payment.paymentId(), visitOnDateElement.getVisitId()));
                });
                return true;
            } else {
                // instead of having all visits paid, maybe paid both separately, so we match payment per visit to see if it matches.
                for (VisitDto visit : visitOnDate) {
                    if (payment.paymentAmount().compareTo(visit.getTotalAmountIncTax()) >= 0) {
                        //      visit.setAmountCovertSoFar(visit.getAmountCovertSoFar().add(payment.paymentAmount()));
                        listIds.add(new PaymentVisitIds(payment.paymentId(), visit.getVisitId()));
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void bulkInsertPaymnetVisits(List<PaymentVisitIds> items) {
        int batchSize = 1000;

        for (int i = 0; i < items.size(); i += batchSize) {
            List<PaymentVisitIds> batch = items.subList(i, Math.min(i + batchSize, items.size()));

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO new_vmas.consult_payment_visit");
            sql.append(" (added_by, added_on, last_edited_by, last_edited_on, version, member_id, payment_id, visit_id) ");
            sql.append(" VALUES ");

            for (int j = 0; j < batch.size(); j++) {
                sql.append("(?, NOW(), ?, NOW(), ?, ?, ?, ?)");
                if (j < batch.size() - 1) {
                    sql.append(", ");
                }
            }
            Query query = entityManager.createNativeQuery(sql.toString());

            int idx = 1;
            for (PaymentVisitIds item : batch) {
                query.setParameter(idx++, "system"); // added_by
                query.setParameter(idx++, "system"); // last_edited_by
                query.setParameter(idx++, 0);        // version
                query.setParameter(idx++, 77L);      // member_id
                query.setParameter(idx++, item.paymentId());
                query.setParameter(idx++, item.visitId());
            }
            query.executeUpdate();

        }
    }
}