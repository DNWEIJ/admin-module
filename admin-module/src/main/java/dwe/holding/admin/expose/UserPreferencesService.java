package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.user.UserRepository;
import dwe.holding.admin.model.User;
import dwe.holding.admin.model.UserPreferences;
import dwe.holding.admin.preferences.UserPreferencesRepository;
import dwe.holding.admin.security.AutorisationUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserPreferencesService {
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPrefRepo;

    public UserPreferencesService(UserRepository userRepository, UserPreferencesRepository userPrefRepo) {
        this.userRepository = userRepository;
        this.userPrefRepo = userPrefRepo;
    }

    public void storeAppPreferences(Long localMemberId, String userPrefJson) {
        // change selected local member
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        User savedUser = userRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);

        // update preferences
        Optional<UserPreferences> optional = userPrefRepo.findByUserIdAndMemberIdAndLocalMemberId(user.getId(), user.getMember().getId(), user.getMemberLocalId());

        UserPreferences userPreferences;
        if (optional.isPresent()) {
            userPreferences = optional.get();
            userPreferences.setUserPreferencesJson(userPrefJson);
        } else {
            userPreferences = UserPreferences.builder()
                    .userId(user.getId())
                    .memberId(user.getMember().getId())
                    .userPreferencesJson(userPrefJson)
                    .build();
        }
        userPreferences = userPrefRepo.save(userPreferences);
        AutorisationUtils.setCurrentUserPref(userPreferences);
    }
}