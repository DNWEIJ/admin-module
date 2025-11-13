package dwe.holding.admin.authorisation.function_role;

import dwe.holding.admin.model.Function;
import dwe.holding.admin.model.User;
import dwe.holding.admin.model.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class FunctionQueryCriteria {
    private final EntityManager entityManager;

    public FunctionQueryCriteria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<Function> process(  Long id) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Function> cq = cb.createQuery(Function.class);

        Root<Function> root = cq.from(Function.class);

        // defining the JOIN clauses
        Join<UserRole, User> user = root.join("functionRoles").join("role").join("userRoles").join("user");
        // what do we want to have in the select
        cq.select(root);
        // what is the where clause
        cq.where(
                cb.equal(user.get("id"), id)
        );
        // execute
        return (entityManager.createQuery(cq)).getResultList();
    }
}