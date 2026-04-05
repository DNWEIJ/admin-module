package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.model.tenant.MemberNoMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNoMemberRepository extends JpaRepository<MemberNoMember, Long> {
}
