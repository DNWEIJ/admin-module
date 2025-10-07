package dwe.holding.generic.admin.authorisation.function_role;

import dwe.holding.generic.admin.model.FunctionRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FunctionRoleRepository extends JpaRepository<FunctionRole, UUID> {
}