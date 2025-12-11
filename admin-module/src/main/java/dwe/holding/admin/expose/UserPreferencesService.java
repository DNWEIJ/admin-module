package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.user.UserRepository;
import dwe.holding.admin.model.MetaUserPreferences;
import dwe.holding.admin.model.User;
import dwe.holding.admin.security.AutorisationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserPreferencesService {
    private final UserRepository userRepository;

    public User storeAppPreferences(String userPrefJson) {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        User savedUser = userRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);

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
       return userRepository.save(user);
    }
}