package dwe.holding.admin.authorisation.user;

import dwe.holding.admin.model.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByAccountWithMemberAndLocals(String account);
}