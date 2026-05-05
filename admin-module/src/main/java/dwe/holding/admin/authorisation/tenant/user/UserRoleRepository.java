package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.model.tenant.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("""
                SELECT ur
                FROM UserRole ur
                JOIN FETCH ur.role
                WHERE ur.user.id = :userId
            """)
    List<UserRole> findByUser_id(Long userId);
}