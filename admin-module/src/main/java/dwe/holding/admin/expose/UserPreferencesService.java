package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.user.UserRepository;
import dwe.holding.admin.model.MetaUserPreferences;
import dwe.holding.admin.model.User;
import dwe.holding.admin.preferences.MetaUserPreferencesRepository;
import dwe.holding.admin.security.AutorisationUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserPreferencesService {
    private final UserRepository userRepository;
    private final MetaUserPreferencesRepository userPrefRepo;

    public UserPreferencesService(UserRepository userRepository, MetaUserPreferencesRepository userPrefRepo) {
        this.userRepository = userRepository;
        this.userPrefRepo = userPrefRepo;
    }

    public void storeAppPreferences(Long localMemberId, String userPrefJson) {
        // change selected local member
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        User savedUser = userRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);

        // update preferences
        Optional<MetaUserPreferences> optional = userPrefRepo.findByUserIdAndMemberIdAndLocalMemberId(user.getId(), user.getMember().getId(), user.getMemberLocalId());

        MetaUserPreferences metaUserPreferences;
        if (optional.isPresent()) {
            metaUserPreferences = optional.get();
            metaUserPreferences.setUserPreferencesJson(userPrefJson);
        } else {
            metaUserPreferences = MetaUserPreferences.builder()
                    .userId(user.getId())
                    .memberId(user.getMember().getId())
                    .userPreferencesJson(userPrefJson)
                    .build();
        }
        metaUserPreferences = userPrefRepo.save(metaUserPreferences);
        AutorisationUtils.setCurrentUserPref(metaUserPreferences);
    }
}