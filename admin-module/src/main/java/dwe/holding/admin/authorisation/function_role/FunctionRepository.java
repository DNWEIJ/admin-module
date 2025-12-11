package dwe.holding.admin.authorisation.function_role;

import dwe.holding.admin.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FunctionRepository extends JpaRepository<Function,   Long> {
    boolean findByName(String name);
}