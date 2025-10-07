package dwe.holding.generic.admin.preferences;

import dwe.holding.generic.admin.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
    List<UserPreferences> findByUserIdAndMemberId(UUID userId, UUID memberId);
   Optional<UserPreferences> findByUserIdAndMemberIdAndLocalMemberId(UUID userId, UUID memberId, UUID localMemberId);
}