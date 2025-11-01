package dwe.holding.generic.admin.authorisation.user;


import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    List<User> findByAccount(String account);

    List<User> findByMember_idAndLoginEnabled(Long memberId, YesNoEnum enabled);
}