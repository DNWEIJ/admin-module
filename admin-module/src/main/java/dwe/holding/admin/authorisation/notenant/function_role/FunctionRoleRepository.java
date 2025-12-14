package dwe.holding.admin.authorisation.notenant.function_role;

import dwe.holding.admin.model.notenant.FunctionRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FunctionRoleRepository extends JpaRepository<FunctionRole,   Long> {
    List<FunctionRole> findByRoleId(Long roleId);
}