package dwe.holding.admin.preferences;

import dwe.holding.admin.model.MetaLocalMemberPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MetaLocalMemberPreferencesRepository extends JpaRepository<MetaLocalMemberPreferences, Long> {
    Optional<MetaLocalMemberPreferences> findByLocalMemberIdAndMemberId(Long localMemberId, Long memberId);
}