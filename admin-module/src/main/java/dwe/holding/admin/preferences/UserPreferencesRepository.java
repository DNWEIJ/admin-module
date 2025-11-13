package dwe.holding.admin.preferences;

import dwe.holding.admin.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserPreferencesRepository extends JpaRepository<UserPreferences,   Long> {
    List<UserPreferences> findByUserIdAndMemberId(  Long userId,   Long memberId);
   Optional<UserPreferences> findByUserIdAndMemberIdAndLocalMemberId(  Long userId,   Long memberId,   Long localMemberId);
}