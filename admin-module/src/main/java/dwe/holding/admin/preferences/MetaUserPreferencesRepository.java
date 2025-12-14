package dwe.holding.admin.preferences;

import dwe.holding.admin.model.tenant.MetaUserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MetaUserPreferencesRepository extends JpaRepository<MetaUserPreferences, Long> {
    Optional<MetaUserPreferences> findByUserIdAndMemberId(Long userId, Long memberId);
}