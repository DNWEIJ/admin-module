package dwe.holding.admin.preferences;

import dwe.holding.admin.model.MetaUserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MetaUserPreferencesRepository extends JpaRepository<MetaUserPreferences,   Long> {
    List<MetaUserPreferences> findByUserIdAndMemberId(Long userId, Long memberId);
   Optional<MetaUserPreferences> findByUserIdAndMemberIdAndLocalMemberId(Long userId, Long memberId, Long localMemberId);
}