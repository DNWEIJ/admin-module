package dwe.holding.admin.authorisation.user;

import dwe.holding.admin.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.util.List;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> findByAccountWithMemberAndLocals(String account) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);

        // fetch associations (equivalent aan JOIN FETCH in JPQL)
        Fetch<User, ?> memberFetch = userRoot.fetch("member", JoinType.INNER);
        memberFetch.fetch("localMembers", JoinType.LEFT);

        cq.select(userRoot).distinct(true)
                .where(cb.equal(userRoot.get("account"), account));

        return em.createQuery(cq).getResultList();
    }

}