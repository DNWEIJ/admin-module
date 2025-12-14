package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<DoubleText> getStaffMembers(Long memberId) {
        return userRepository.findByMember_idAndLoginEnabled(memberId, YesNoEnum.Yes)
                .stream().map(rec -> new DoubleText(rec.getName(), rec.getName())).toList();
    }

    public record DoubleText(String id, String name) {
    }

    public void save(Long localMemberId, LanguagePrefEnum language) {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).orElseThrow();
        user.setLocalMemberId(localMemberId);
        user.setLanguage(language);
        userRepository.save(user);
    }

    public User setLocalMemberId(Long localMemberId) {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).orElseThrow();
        user.setLocalMemberId(localMemberId);
        return userRepository.save(user);
    }
}