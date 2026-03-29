package dwe.holding.reporting.repository.dsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import dwe.holding.customer.client.model.QCustomer;
import dwe.holding.customer.client.model.QPet;
import dwe.holding.customer.client.model.QReminder;
import dwe.holding.reporting.repository.projection.RemindersListProjection;
import dwe.holding.salesconsult.consult.model.QAppointment;
import dwe.holding.salesconsult.consult.model.QVisit;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReminderListDsl {

    private final EntityManager em;

    public List<RemindersListProjection> findReminders(Long memberId, LocalDate fromDate, LocalDate toDate,
                                                       List<String> species, List<String> reminders,
                                                       String visitDateBeforeAfterDueDate, String lastName, boolean petsAreAlive) {

        JPAQuery<RemindersListProjection> query = new JPAQuery<>(em);

        QAppointment appointment = QAppointment.appointment;
        QVisit visit = QVisit.visit;
        QPet pet = QPet.pet;
        QCustomer customer = QCustomer.customer;
        QReminder reminder = QReminder.reminder;
        BooleanBuilder where = new BooleanBuilder();

        query
                .from(reminder)
                .join(reminder.pet, pet)
                .join(pet.customer, customer);

        // memberId filters
        where.and(reminder.memberId.eq(memberId)
                .and(pet.memberId.eq(memberId))
                .and(customer.memberId.eq(memberId)));

        // species filter
        if (species != null && !species.isEmpty()) {
            where.and(pet.species.in(species));
        }

        // reminder filter
        if (reminders != null && !reminders.isEmpty()) {
            where.and(reminder.reminderText.in(reminders));
        }
        // pet Alive filter
        if (petsAreAlive) {
            where.and(pet.deceased.eq(YesNoEnum.No));
        }
        // lastname
        if (lastName != null && !lastName.isEmpty()) {
            where.and(customer.lastName.startsWith(lastName));
        }
        // date filters
        if (fromDate != null) {
            where.and(reminder.dueDate.goe(fromDate));
        }
        if (toDate != null) {
            where.and(reminder.dueDate.loe(toDate));
        }

        // projection
        query.select(Projections.constructor(
                RemindersListProjection.class,
                pet.id,
                pet.name,
                pet.species,
                pet.breed,

                customer.id,
                customer.lastName,
                customer.firstName,
                customer.surName,
                customer.middleInitial,
                customer.email,
                customer.mobilePhone,
                customer.workPhone,

                reminder.dueDate,
                reminder.reminderText,
                reminder.id,
                reminder.originatingAppointmentId
        ));

        query.where(where)
                .groupBy(reminder.id)
                .orderBy(reminder.dueDate.asc());
        return query.fetch();

    }
}