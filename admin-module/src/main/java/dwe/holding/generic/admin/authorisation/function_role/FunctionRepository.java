package dwe.holding.generic.admin.authorisation.function_role;


import dwe.holding.generic.admin.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;

  

public interface FunctionRepository extends JpaRepository<Function,   Long> {
}