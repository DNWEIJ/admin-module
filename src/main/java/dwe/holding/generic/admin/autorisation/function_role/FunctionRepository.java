package dwe.holding.generic.admin.autorisation.function_role;


import dwe.holding.generic.admin.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FunctionRepository extends JpaRepository<Function, UUID> {
}