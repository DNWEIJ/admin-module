package dwe.holding.generic.admin.authorisation.user;

import dwe.holding.generic.admin.model.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByAccountWithMemberAndLocals(String account);
}