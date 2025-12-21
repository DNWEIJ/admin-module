package dwe.holding.admin.authorisation.tenant.user;


import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByAccount(String account);

    List<User> findByMemberIdAndLoginEnabledAndPersonnelStatusNotOrderByName(Long memberId, YesNoEnum enabled, PersonnelStatusEnum personnelStatus);
}