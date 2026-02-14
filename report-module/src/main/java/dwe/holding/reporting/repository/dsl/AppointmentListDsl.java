package dwe.holding.reporting.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;

import dwe.holding.customer.client.model.QCustomer;
import dwe.holding.customer.client.model.QPet;
import dwe.holding.reporting.repository.projection.VisitListProjection;
import dwe.holding.salesconsult.consult.model.QAppointment;
import dwe.holding.salesconsult.consult.model.QVisit;
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

    public List<VisitListProjection> findVisits(Long memberId, Long localMemberId, LocalDate fromDate, LocalDate toDate) {

        JPAQuery<VisitListProjection> query = new JPAQuery<>(em);

        QAppointment appointment = QAppointment.appointment;
        QVisit visit = QVisit.visit;
        QPet pet = QPet.pet;
        QCustomer customer = QCustomer.customer;

        NumberExpression<BigDecimal> totalAmountExpr = Expressions.numberTemplate(
                BigDecimal.class,
                "coalesce((select sum(li.totalIncTax) from LineItem li where li.appointment.id = {0} and li.pet.id = {1}), 0)",
                appointment.id,
                pet.id
        );

        NumberExpression<BigDecimal> paidAmountExpr = Expressions.numberTemplate(
                BigDecimal.class,
                "coalesce((select sum(p.amount) from Payment p join p.paymentVisits pv where pv.visit.id = {0}), 0)",
                visit.id
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

                        totalAmountExpr,
                        paidAmountExpr
                ))
                .from(appointment)
                .join(visit).on(visit.appointment.id.eq(appointment.id))
                .join(pet).on(visit.pet.id.eq(pet.id))
                .join(customer).on(pet.customer.id.eq(customer.id))
                .where(
                        appointment.visitDateTime.between(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)),
                        appointment.memberId.eq(memberId),
                        appointment.localMemberId.eq(localMemberId)
                )
                .fetch();
    }
}
