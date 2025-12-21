package dwe.holding.admin.transactional;


import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.localmember.LocalMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.model.notenant.Member;
import dwe.holding.admin.model.tenant.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TransactionalUserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localMemberRepository;

    public List<User> getByAccount(String account) {
        List<User>  users = userRepository.findByAccount(account);
        users.forEach(user -> user.setMember(memberRepository.findById(user.getMemberId()).orElseThrow()));
        return users;
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