package dwe.holding.generic.admin.autorisation.function_role;


import dwe.holding.generic.admin.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}