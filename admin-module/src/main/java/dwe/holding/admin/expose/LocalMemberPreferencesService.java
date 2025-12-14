package dwe.holding.admin.expose;

import dwe.holding.admin.authorisation.tenant.localmember.LocalMemberRepository;
import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.admin.model.tenant.MetaLocalMemberPreferences;
import dwe.holding.admin.preferences.MetaLocalMemberPreferencesRepository;
import dwe.holding.admin.security.AutorisationUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LocalMemberPreferencesService {
    private final LocalMemberRepository localMemberRepository;
    private final MetaLocalMemberPreferencesRepository localMemberPrefRepository;

    public LocalMemberPreferencesService(LocalMemberRepository localMemberRepository, MetaLocalMemberPreferencesRepository userPrefRepo) {
        this.localMemberRepository = localMemberRepository;
        this.localMemberPrefRepository = userPrefRepo;
    }

    public void storeAppPreferences(String userPrefJson) {
        LocalMember localMember = localMemberRepository.findById(AutorisationUtils.getCurrentUserMlid()).get();
        // update preferences
        Optional<MetaLocalMemberPreferences> optional = localMemberPrefRepository.findByLocalMemberIdAndMemberId(localMember.getId(), localMember.getMemberId());

        MetaLocalMemberPreferences metaLocalMemberPreferences;
        if (optional.isPresent()) {
            metaLocalMemberPreferences = optional.get();
            metaLocalMemberPreferences.setPreferencesJson(userPrefJson);
        } else {
            metaLocalMemberPreferences = MetaLocalMemberPreferences.builder()
                    .preferencesJson(userPrefJson)
                    .localMember(localMember)
                    .memberId(AutorisationUtils.getCurrentUserMid())
                    .build();
        }
        metaLocalMemberPreferences = localMemberPrefRepository.save(metaLocalMemberPreferences);
    }
}