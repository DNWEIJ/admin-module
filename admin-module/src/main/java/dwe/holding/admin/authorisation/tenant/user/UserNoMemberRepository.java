package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.model.tenant.UserNoMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNoMemberRepository extends JpaRepository<UserNoMember, Long> {
    List<UserNoMember> findByAccount(String account);
}