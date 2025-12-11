package dwe.holding.admin.transactional;


import dwe.holding.admin.authorisation.user.UserRepository;
import dwe.holding.admin.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TransactionalUser {

    private final UserRepository userRepository;

    public List<User> getByAccount(String account) {
        return userRepository.findByAccountWithMemberAndLocals(account);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User getByIdLazy_LoadingAllData(Long id) {
        User user = userRepository.findById(id).get();
        user.setRoles(user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList());
        user.getIpNumbers().size();
        user.getMember().getShortCode();
        user.getMember().getLocalMembers().size();
        user.getMember().getLocalMembers().forEach(localMember -> localMember.getMemberLocalTaxs().size());
        return user;
    }

    public Object findAll() {
        return userRepository.findAll();
    }
}