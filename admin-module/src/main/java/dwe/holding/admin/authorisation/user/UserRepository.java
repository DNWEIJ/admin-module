package dwe.holding.admin.authorisation.user;


import dwe.holding.admin.model.User;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    List<User> findByAccount(String account);

    List<User> findByMember_idAndLoginEnabled(Long memberId, YesNoEnum enabled);
}