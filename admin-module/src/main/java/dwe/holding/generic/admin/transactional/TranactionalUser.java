package dwe.holding.generic.admin.transactional;


import dwe.holding.generic.admin.authorisation.user.UserRepository;
import dwe.holding.generic.admin.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TranactionalUser {

    private final UserRepository userRepository;

    public List<User> getByAccount(String account) {
        return userRepository.findByAccountWithMemberAndLocals(account);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User getByIdLazy_RolesAndIpNumbers(Long id) {
        User user = userRepository.findById(id).get();
        user.setRoles(user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList());
        user.getIpNumbers().size();
        user.getMember().getShortCode();
        return user;
    }

    public Object findAll() {
        return userRepository.findAll();
    }
}