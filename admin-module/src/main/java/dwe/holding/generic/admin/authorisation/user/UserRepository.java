package dwe.holding.generic.admin.authorisation.user;


import dwe.holding.generic.admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
  

public interface UserRepository extends JpaRepository<User,   Long> , UserRepositoryCustom {
    List<User> findByAccount(String account);
}