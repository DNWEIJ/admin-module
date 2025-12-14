package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.model.tenant.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRoleRepository extends JpaRepository<UserRole,   Long> {
}