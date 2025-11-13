package dwe.holding.admin.authorisation.function_role;

import dwe.holding.admin.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRoleRepository extends JpaRepository<UserRole,   Long> {
}