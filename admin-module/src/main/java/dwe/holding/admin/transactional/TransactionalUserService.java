package dwe.holding.admin.transactional;


import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.localmember.LocalMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.MemberNoMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.UserNoMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.model.notenant.Member;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserNoMember;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TransactionalUserService {

    private final UserRepository userRepository;
    private final UserNoMemberRepository userNoMemberRepository;
    private final MemberNoMemberRepository memberNoMemberRepository;
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localMemberRepository;

    /**
     * User is connected to the member.
     * So, memberId will be set via tenant connection. However the user isn't logged in at the moment, therefore
     * @param account
     * @return
     */
    public List<UserNoMember> getByAccount(String account) {

        List<UserNoMember>  userNoMembers = userNoMemberRepository.findByAccount(account);
        // picking up member manual, to create the 'static' memberList in order to save query time
        userNoMembers.forEach(userNomem -> userNomem.setMember(memberNoMemberRepository.findById(userNomem.getMemberId()).orElseThrow()));
        return userNoMembers;
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User getByIdLazy_LoadingAllData(Long id) {
        User user = userRepository.findById(id).get();
        user.setRoles(user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList());
        user.getIpNumbers().size();
        Member member = memberRepository.findById(user.getMemberId()).orElseThrow();
        user.setMember(member);
        user.getMember().setLocalMembers(localMemberRepository.findByMemberId(member.getId()));
        user.getMetaUserPreferences()   ;
        return user;
    }

    public Object findAll() {
        return userRepository.findAll();
    }
}