package dwe.holding.salesconsult.consult.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import dwe.holding.customer.client.model.QCustomer;
import dwe.holding.customer.client.model.QPet;
import dwe.holding.salesconsult.consult.model.QAppointment;
import dwe.holding.salesconsult.consult.model.QPaymentVisit;
import dwe.holding.salesconsult.consult.model.QVisit;
import dwe.holding.salesconsult.consult.repository.VisitListProjection;
import dwe.holding.salesconsult.sales.model.QLineItem;
import dwe.holding.salesconsult.sales.model.QPayment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentListDsl {

    private final EntityManager em;

    public List<VisitListProjection> findVisits(Long mid, Long mlid, LocalDate fromDate, LocalDate toDate) {

        JPAQuery<VisitListProjection> query = new JPAQuery<>(em);

        QAppointment appointment = QAppointment.appointment;
        QVisit visit = QVisit.visit;
        QPet pet = QPet.pet;
        QCustomer customer = QCustomer.customer;
        QLineItem lineItem = QLineItem.lineItem;
        QPaymentVisit paymentVisit = QPaymentVisit.paymentVisit;
        QPayment payment = QPayment.payment;

        // Use numberTemplate for aggregations in QueryDSL 7.x
        NumberExpression<BigDecimal> totalAmountExpr = Expressions.numberTemplate(
                BigDecimal.class,
                "coalesce(sum({0}), 0)",
                lineItem.totalIncTax
        );

        NumberExpression<BigDecimal> paidAmountExpr = Expressions.numberTemplate(
                BigDecimal.class,
                "coalesce(sum({0}), 0)",
                payment.amount
        );

        List<VisitListProjection> results = query
                .select(Projections.constructor(
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

                        pet.id,
                        pet.name,
                        pet.species,
                        pet.breed,

                        customer.id,
                        customer.lastName,
                        customer.firstName,
                        customer.surName,
                        customer.middleInitial,

                        totalAmountExpr,
                        paidAmountExpr
                ))
                .from(appointment)
                .join(visit).on(visit.appointment.id.eq(appointment.id))
                .join(pet).on(visit.pet.id.eq(pet.id))
                .join(customer).on(pet.customer.id.eq(customer.id))
                .leftJoin(lineItem)
                .on(lineItem.appointment.id.eq(appointment.id)
                        .and(lineItem.pet.id.eq(pet.id)))
                .leftJoin(paymentVisit)
                .on(paymentVisit.visit.id.eq(visit.id))
                .leftJoin(paymentVisit.payment, payment)
                .where(
                        appointment.visitDateTime.between(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)),
                        appointment.memberId.eq(mid),
                        appointment.localMemberId.eq(mlid)
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

                        pet.id,
                        pet.name,
                        pet.species,
                        pet.breed,

                        customer.id,
                        customer.lastName,
                        customer.firstName,
                        customer.surName,
                        customer.middleInitial
                )
                .fetch();

        return results;
    }

}

