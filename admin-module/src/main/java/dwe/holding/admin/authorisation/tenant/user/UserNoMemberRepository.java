package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.model.tenant.IPSecurity;
import dwe.holding.admin.model.tenant.UserNoMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNoMemberRepository extends JpaRepository<UserNoMember, Long> {
    List<UserNoMember> findByAccount(String account);

    @Query("SELECT u.ipNumbers FROM UserNoMember u WHERE u.id = :userId")
    List<IPSecurity> getIpNumbersForUserId(Long userId);
}