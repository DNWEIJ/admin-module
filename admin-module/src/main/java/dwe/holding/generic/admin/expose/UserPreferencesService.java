package dwe.holding.generic.admin.expose;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.generic.admin.authorisation.user.UserRepository;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserPreferences;
import dwe.holding.generic.admin.preferences.UserPreferencesRepository;
import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserPreferencesService {
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPrefRepo;
    private final ObjectMapper objectMapper;

    public UserPreferencesService(UserRepository userRepository, UserPreferencesRepository userPrefRepo, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userPrefRepo = userPrefRepo;
        this.objectMapper = objectMapper;
    }

    public void storeAppPreferences(Long localMemberId, String userPrefJson) {
        // change selected local member
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        user.setMemberLocalId(AutorisationUtils.validateAndreturnLocalMemberId(localMemberId));
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
                    .localMemberId(user.getMemberLocalId())
                    .memberId(user.getMember().getId())
                    .userPreferencesJson(userPrefJson)
                    .build();
        }
        userPreferences = userPrefRepo.save(userPreferences);
        AutorisationUtils.setCurrentUserPref(userPreferences);
    }
}