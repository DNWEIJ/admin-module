package dwe.holding.reporting.repository.dsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQuery;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.QCustomer;
import dwe.holding.customer.client.model.QPet;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.reporting.controller.ReportCustomersController;
import dwe.holding.reporting.repository.projection.PaymentListProjection;
import dwe.holding.reporting.repository.projection.VisitListProjection;
import dwe.holding.salesconsult.consult.model.QAppointment;
import dwe.holding.salesconsult.consult.model.QPaymentVisit;
import dwe.holding.salesconsult.consult.model.QVisit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.sales.model.QLineItem;
import dwe.holding.salesconsult.sales.model.QPayment;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EntityListDsls {

    private final EntityManager em;
    private final String DONOTCARE = "doNotCare";
    private final String Yes = "Yes";
    private final String No = "No";

    public List<Customer> findCustomers(Long memberId, ReportCustomersController.DocForm docForm) {
        JPAQuery<Customer> query = new JPAQuery<>(em);

        QCustomer customer = QCustomer.customer;
        QPet pet = QPet.pet;
        QVisit visit = QVisit.visit;
        QAppointment appointment = QAppointment.appointment;

        LocalDate now = LocalDate.now();
        LocalDate oldestBirthday = docForm.minAgePetInMonths() != null ? now.minusMonths(docForm.minAgePetInMonths()) : null;
        LocalDate youngestBirthday = docForm.maxAgePetInMonths() != null ? now.minusMonths(docForm.maxAgePetInMonths()) : null;

        LocalDateTime cutOffDateTime = LocalDateTime.now().minusMonths(docForm.nrOfMonths());

        return query
                .from(customer)
                .join(pet).on(customer.id.eq(pet.customer.id))
                .join(visit).on(visit.pet.id.eq(pet.id))
                .join(appointment).on(visit.appointment.id.eq(appointment.id))
                .where(
                        // appointment
                        appointment.visitDateTime.goe(cutOffDateTime),
                        appointment.memberId.eq(memberId),

                        // Customer
                        customer.memberId.eq(memberId),
                        (docForm.customerActive().equals(DONOTCARE) ? null :
                                docForm.customerActive().equals(Yes) ? customer.status.ne(CustomerStatusEnum.CLOSED) : customer.status.eq(CustomerStatusEnum.CLOSED)),
                        (docForm.emailAddress().equals(DONOTCARE) ? null :
                                docForm.emailAddress().equals(Yes) ? customer.email.isNotNull().and(customer.email.isNotEmpty()) : customer.email.isEmpty()),
                        (docForm.newsLetter().equals(DONOTCARE) ? null :
                                docForm.newsLetter().equals(Yes) ? customer.newsletter.eq(YesNoEnum.Yes) : customer.newsletter.eq(YesNoEnum.No)),

                        // Pet
                        pet.memberId.eq(memberId),
                        (docForm.certainPetAge().equals(DONOTCARE) ? null : docForm.certainPetAge().equals(No) ? null : pet.birthday.between(oldestBirthday, youngestBirthday)),
                        (docForm.onlyLivingPets().equals(DONOTCARE) ? null : docForm.onlyLivingPets().equals(Yes) ?
                                (pet.deceased.eq(YesNoEnum.No).and(pet.deceasedDate.isNull())) : pet.deceased.eq(YesNoEnum.Yes).or(pet.deceasedDate.isNotNull())),

                        (docForm.sexType().equals(DONOTCARE) ? null : pet.sex.eq(SexTypeEnum.valueOf(docForm.sexType()))),
                        (docForm.species() == null ? null : pet.species.in(docForm.species()))
                )
                .transform(
                        GroupBy.groupBy(customer.id).list(
                                Projections.constructor(Customer.class,
                                        customer.id,
                                        customer.firstName,
                                        customer.surName,
                                        customer.lastName,
                                        customer.middleInitial,
                                        customer.email,
                                        customer.newsletter,
                                        customer.homePhone,
                                        customer.workPhone,
                                        customer.mobilePhone,
                                        customer.address2,
                                        customer.city,
                                        customer.zipCode,
                                        customer.status,
                                        customer.balance,
                                        GroupBy.set(
                                                Projections.constructor(Pet.class,
                                                        pet.id,
                                                        pet.name,
                                                        pet.birthday,
                                                        pet.deceased,
                                                        pet.deceasedDate,
                                                        pet.allergies,
                                                        pet.allergiesDescription,
                                                        pet.gpwarning,
                                                        pet.gpwarningDescription,
                                                        pet.insured,
                                                        pet.insuredBy,
                                                        pet.passportNumber,
                                                        pet.chipDate,
                                                        pet.chipTattooId,
                                                        pet.briefDescription,
                                                        pet.species,
                                                        pet.breed,
                                                        pet.sex,
                                                        pet.idealWeight
                                                )
                                        )
                                )
                        )
                );
    }


    public List<VisitListProjection> findPaymentsNoVisit(Long memberId, Long localMemberId, LocalDate fromDate, LocalDate toDate) {
        JPAQuery<VisitListProjection> query = new JPAQuery<>(em);

        QCustomer customer = QCustomer.customer;
        QPayment payment = QPayment.payment;


        return query.select(Projections.constructor(
                        VisitListProjection.class,
                        Expressions.constant(0L),
                        payment.paymentDate,
                        Expressions.constant(YesNoEnum.No),
                        Expressions.constant(YesNoEnum.No),
                        Expressions.constant(YesNoEnum.No),

                        Expressions.constant(0L),
                        Expressions.constant(0),
                        Expressions.constant(""),
                        Expressions.constant(""),
                        Expressions.constant(""),
                        Expressions.constant(VisitStatusEnum.WAITING),
                        Expressions.constant(YesNoEnum.No),

                        Expressions.constant(0L),
                        Expressions.constant(""),
                        Expressions.constant(""),
                        Expressions.constant(""),
                        Expressions.constant(YesNoEnum.No),
                        Expressions.constant(""),

                        customer.id,
                        customer.lastName,
                        customer.firstName,
                        customer.surName,
                        customer.middleInitial,

                        Expressions.constant(BigDecimal.ZERO),
                        payment.amount
                ))
                .from(payment)
                .join(customer).on(payment.customer.id.eq(customer.id))
                .where(
                        customer.memberId.eq(memberId),
                        payment.paymentDate.between(fromDate, toDate),
                        localMemberId == 0 ? null : payment.localMemberId.eq(localMemberId),
                        payment.paymentVisits.isEmpty()

                )
                .orderBy(payment.paymentDate.asc())
                .setHint("javax.persistence.query.hint", "STRAIGHT_JOIN")
                .fetch();
    }

    public List<VisitListProjection> findVisits(Long memberId, Long localMemberId, LocalDate fromDate, LocalDate toDate) {
        JPAQuery<VisitListProjection> query = new JPAQuery<>(em);

        QAppointment appointment = QAppointment.appointment;
        QVisit visit = QVisit.visit;
        QPet pet = QPet.pet;
        QCustomer customer = QCustomer.customer;

        QPaymentVisit paymentVisitSubQuery = new QPaymentVisit("pvSub");
        QPayment paymentSubQuery = new QPayment("pSub");

        NumberExpression<BigDecimal> totalPaid = Expressions.numberTemplate(BigDecimal.class,
                "({0})",
                JPAExpressions
                        .select(paymentSubQuery.amount.sumAggregate().coalesce(BigDecimal.ZERO))
                        .from(paymentVisitSubQuery)
                        .join(paymentSubQuery).on(paymentSubQuery.id.eq(paymentVisitSubQuery.payment.id))
                        .where(paymentVisitSubQuery.visit.id.eq(visit.id))
        );

        return query.select(Projections.constructor(
                        VisitListProjection.class,
                        appointment.id,
                        appointment.visitDateTime,
                        appointment.cancelled,
                        appointment.completed,
                        appointment.OTC,

                        visit.id,
                        visit.estimatedTimeInMinutes,
                        visit.veterinarian,
                        visit.purpose,
                        visit.room,
                        visit.status,
                        visit.sentToInsurance,

                        pet.id,
                        pet.name,
                        pet.species,
                        pet.breed,
                        pet.insured,
                        pet.insuredBy,

                        customer.id,
                        customer.lastName,
                        customer.firstName,
                        customer.surName,
                        customer.middleInitial,

                        visit.totalAmountIncTax,
                        // TODO add tax
                        totalPaid
                ))
                .from(appointment)
                .join(visit).on(visit.appointment.id.eq(appointment.id))
                .join(pet).on(visit.pet.id.eq(pet.id))
                .join(customer).on(pet.customer.id.eq(customer.id))
                .where(
                        appointment.visitDateTime.between(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)),
                        appointment.memberId.eq(memberId),
                        localMemberId == 0 ? null : appointment.localMemberId.eq(localMemberId)
                )
                .groupBy(
                        appointment.id,
                        appointment.visitDateTime,
                        appointment.cancelled,
                        appointment.completed,
                        appointment.OTC,

                        visit.id,
                        visit.estimatedTimeInMinutes,
                        visit.veterinarian,
                        visit.purpose,
                        visit.room,
                        visit.status,
                        visit.sentToInsurance,

                        pet.id,
                        pet.name,
                        pet.species,
                        pet.breed,
                        pet.insured,
                        pet.insuredBy,

                        customer.id,
                        customer.lastName,
                        customer.firstName,
                        customer.surName,
                        customer.middleInitial
                )
                .orderBy(appointment.visitDateTime.asc())
                .setHint("javax.persistence.query.hint", "STRAIGHT_JOIN")
                .fetch();
    }

    public List<PaymentListProjection> findPaymentsWithBalance(Long memberId, Long localMemberId, LocalDate fromDate, LocalDate toDate) {

        List<PaymentListProjection> records = paymentsWithBalance(memberId, localMemberId, fromDate, toDate);

        records.stream().forEach(p -> p.setBalanceFromDate(
                customerBalance(p.getCustomerId(), localMemberId, p.getPaymentDate())
        ));
        return records;

    }

    private List<PaymentListProjection> paymentsWithBalance(Long memberId, Long localMemberId, LocalDate fromDate, LocalDate toDate) {

        JPAQuery<Customer> query = new JPAQuery<>(em);
        QPayment payment = QPayment.payment;
        QCustomer customer = QCustomer.customer;

        return query
                .select(Projections.constructor(
                                PaymentListProjection.class,
                                payment.paymentDate,
                                payment.localMemberId,
                                payment.referenceNumber,
                                payment.method,
                                payment.amount,
                                customer.id,
                                customer.lastName,
                                customer.firstName,
                                customer.surName,
                                customer.middleInitial,
                                customer.balance,
                                customer.balance
                        )
                )
                .from(payment)
                .join(customer).on(customer.id.eq(payment.customer.id))
                .where(
                        payment.paymentDate.between(fromDate, toDate),
                        payment.memberId.eq(memberId),
                        (localMemberId == null) ? null : payment.localMemberId.eq(localMemberId)
                )
                .orderBy(payment.paymentDate.asc())
                .fetch();
    }

    private BigDecimal customerBalance(Long customerId, Long memberLocalId, LocalDate toDate) {
        // todo ask claude
        QPayment p = QPayment.payment;
        QLineItem li = QLineItem.lineItem;
        QPet pet = QPet.pet;
        QAppointment a = QAppointment.appointment;

        // --- Payment predicate
        BooleanBuilder paymentPredicate = new BooleanBuilder()
                .and(p.customer.id.eq(customerId));

        if (memberLocalId != null) {
            paymentPredicate.and(p.localMemberId.eq(memberLocalId));
        }

        if (toDate != null) {
            paymentPredicate.and(p.paymentDate.loe(toDate));
        }

        // --- LineItem predicate
        BooleanBuilder lineItemPredicate = new BooleanBuilder()
                .and(pet.customer.id.eq(customerId));

        if (memberLocalId != null) {
            lineItemPredicate.and(a.localMemberId.eq(memberLocalId));
        }

        if (toDate != null) {
            lineItemPredicate.and(a.visitDateTime.loe(toDate.atStartOfDay()));
        }

        // --- Subqueries
        JPQLSubQuery<BigDecimal> paymentsSubquery = JPAExpressions
                .select(p.amount.sumAggregate().coalesce(BigDecimal.ZERO))
                .from(p)
                .where(paymentPredicate);

        JPQLSubQuery<BigDecimal> lineItemsSubquery = JPAExpressions
                .select(li.totalIncTax.sumAggregate().coalesce(BigDecimal.ZERO))
                .from(li)
                .join(li.pet, pet)
                .join(li.appointment, a)
                .where(lineItemPredicate);


        return null;
    }
}


//        SELECT TP.PaymentID,TP.PaymentDate,ML.Clinic,TP.Method,
//                TP.ReferenceNumber,TP.Amount,TP.Owner,
//                FuncOwnerBalanceAtMLID(TP.OwnerID,null,null) AS Balance,
//        FuncOwnerBalanceAtMLID(TP.OwnerID,Date(TP.PaymentDate),null) AS BalanceAsOf,flag
//        FROM TMP_Payment_Listing TP
//        LEFT JOIN thau_MemberLocal ML ON (TP.MLID = ML.memberlocal_id)
//                --  ORDER BY TP.PaymentDate DESC,TP.PaymentID DESC;
//        ORDER BY TP.paymentdate, TP.method DESC;

