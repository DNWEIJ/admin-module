package dwe.holding.generic.admin.expose;

import dwe.holding.generic.admin.authorisation.user.UserRepository;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getStaffMembers(Long memberId) {
        return userRepository.findByMember_idAndLoginEnabled(memberId, YesNoEnum.Yes);
    }
}