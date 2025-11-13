package dwe.holding.admin.authorisation.function_role;


import dwe.holding.admin.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RoleRepository extends JpaRepository<Role,   Long> {
    Role getRoleByName(String aDefault);
}