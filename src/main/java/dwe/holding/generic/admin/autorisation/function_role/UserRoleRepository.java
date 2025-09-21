package dwe.holding.generic.admin.autorisation.function_role;

import dwe.holding.generic.admin.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}