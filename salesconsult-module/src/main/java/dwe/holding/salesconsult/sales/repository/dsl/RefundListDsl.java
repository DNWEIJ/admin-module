package dwe.holding.salesconsult.sales.repository.dsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.QCustomer;
import dwe.holding.salesconsult.sales.model.QRefund;
import dwe.holding.salesconsult.sales.repository.projection.RefundProjection;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RefundListDsl {

    private final EntityManager em;

    public List<RefundProjection> findRefunds(Long localMemberId, LocalDate fromDate, LocalDate toDate) {

        JPAQuery<RefundProjection> query = new JPAQuery<>(em);
        BooleanBuilder where = new BooleanBuilder();

        QCustomer customer = QCustomer.customer;
        QRefund refund = QRefund.refund;

        query
                .from(refund)
                .join(customer)
                .on(refund.customerId.eq(customer.id));

        where.and(refund.memberId.eq(AutorisationUtils.getCurrentUserMid()).and(customer.memberId.eq(AutorisationUtils.getCurrentUserMid())));

        if (localMemberId != null && localMemberId != 0) {
            where.and(refund.localMemberId.eq(localMemberId));
        }
        if (fromDate != null) {
            where.and(refund.refundDate.goe(fromDate));
        }
        if (toDate != null) {
            where.and(refund.refundDate.loe(toDate));
        }

        // projection
        query.select(Projections.constructor(
                RefundProjection.class,
                refund.id,
                customer.id,
                customer.lastName,
                customer.firstName,
                customer.surName,
                customer.middleInitial,
                refund.refundDate,
                refund.amount,
                refund.comments,
                refund.localMemberId
        ));
        query.where(where)
                .orderBy(refund.refundDate.asc());
        return query.fetch();

    }
}