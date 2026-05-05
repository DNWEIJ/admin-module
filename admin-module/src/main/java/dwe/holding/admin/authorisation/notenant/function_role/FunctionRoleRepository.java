package dwe.holding.admin.authorisation.notenant.function_role;

import dwe.holding.admin.model.notenant.FunctionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FunctionRoleRepository extends JpaRepository<FunctionRole, Long> {
    List<FunctionRole> findByRoleId(Long roleId);

    // due to two entity managers, we use native query
    @Query(value = """
            SELECT r.id
            FROM ADMIN_FUNCTION_ROLE fr
            JOIN ADMIN_ROLE r ON r.id = fr.role_id
            WHERE fr.function_id = :functionId
            """, nativeQuery = true)
    List<Long> findRolesByFunctionId(Long functionId);
}