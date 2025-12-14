package dwe.holding.admin.authorisation.tenant.role;

import dwe.holding.admin.model.notenant.Function;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FunctionRepository extends JpaRepository<Function,   Long> {
    boolean findByName(String name);
}