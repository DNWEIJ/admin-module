package dwe.holding.generic.admin.autorisation.user;


import dwe.holding.generic.admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> , UserRepositoryCustom {
    List<User> findByAccount(String account);
}