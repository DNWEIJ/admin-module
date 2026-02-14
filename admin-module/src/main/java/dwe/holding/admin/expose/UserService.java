package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.model.tenant.MetaUserPreferences;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.admin.transactional.TransactionalUserService;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TransactionalUserService userService;

    public List<PresentationElement> getStaffMembers(Long memberId) {
        return userRepository.findByMemberIdAndLoginEnabledAndPersonnelStatusNotOrderByName(memberId, YesNoEnum.Yes, PersonnelStatusEnum.Other)
                .stream().map(rec -> new PresentationElement(rec.getName(), rec.getName())).toList();
    }

    public void save(Long localMemberId, LanguagePrefEnum language) {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).orElseThrow();
        user.setLocalMemberId(localMemberId);
        user.setLanguage(language);
        userRepository.save(user);
    }

    public User setLocalMemberId(Long localMemberId) {
        User user = userService.getByIdLazy_LoadingAllData(AutorisationUtils.getCurrentUserId());
        user.setLocalMemberId(localMemberId);
        return userRepository.save(user);
    }

    public void saveUserSettings(String userPrefJson) {
        saveUserSettings(userPrefJson, 0L, null, null, null);
    }

    public void saveUserSettings(String userPrefJson, Long localMemberId, LanguagePrefEnum language, String username, String email) {
        User user = userService.getByIdLazy_LoadingAllData(AutorisationUtils.getCurrentUserId());
        // update preferences
        if (user.getMetaUserPreferences() == null) {
            user.setMetaUserPreferences(MetaUserPreferences.builder()
                    .user(user)
                    .preferencesJson(userPrefJson)
                    .build()
            );
        } else {
            user.getMetaUserPreferences().setPreferencesJson(userPrefJson);
        }
        if (localMemberId != 0) user.setLocalMemberId(localMemberId);
        if (language != null) user.setLanguage(language);
        if (username != null) user.setName(username);
        if (email != null) user.setEmail(email);
        User savedUser = userRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);
    }
}