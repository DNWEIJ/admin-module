package dwe.holding.admin.authorisation.tenant.user;


import dwe.holding.admin.model.tenant.User;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByAccount(String account);

    // todo: NO OTHER JUST VETS AND ASSISTANCES
    List<User> findByMember_idAndLoginEnabled(Long memberId, YesNoEnum enabled);
}