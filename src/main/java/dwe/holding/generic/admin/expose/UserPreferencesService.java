package dwe.holding.generic.admin.expose;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserPreferences;
import dwe.holding.generic.admin.preferences.UserPreferencesRepository;
import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPreferencesService {
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPrefRepo;
    private final ObjectMapper objectMapper;

    public UserPreferencesService(UserRepository userRepository, UserPreferencesRepository userPrefRepo, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userPrefRepo = userPrefRepo;
        this.objectMapper = objectMapper;
    }


    public void storeAppPreferences(UUID localMemberId, Object userPref) throws JsonProcessingException {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        user.setMemberLocalId(AutorisationUtils.validateAndreturnLocalMemberId(localMemberId));
        User savedUser = userRepository.save(user);

        String stringRepresentation = objectMapper.writeValueAsString(userPref);
        Optional<UserPreferences> optional = userPrefRepo.findByUserIdAndMemberIdAndLocalMemberId(user.getId(), user.getMember().getId(), user.getMemberLocalId());

        UserPreferences userPreferences;
        if (optional.isPresent()) {
            userPreferences = optional.get();
            userPreferences.setUserPreferencesJson(stringRepresentation);

        } else {
            userPreferences = UserPreferences.builder()
                    .userId(user.getId())
                    .localMemberId(user.getMemberLocalId())
                    .memberId(user.getMember().getId())
                    .userPreferencesJson(stringRepresentation)
                    .build();
        }
        userPrefRepo.save(userPreferences);

        AutorisationUtils.setCurrentUser(savedUser);
        AutorisationUtils.setCurrentUserPref(userPref);
    }
}