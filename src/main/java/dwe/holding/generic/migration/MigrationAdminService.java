package dwe.holding.generic.migration;

import dwe.holding.generic.admin.autorisation.function_role.FunctionRepository;
import dwe.holding.generic.admin.autorisation.function_role.FunctionRoleRepository;
import dwe.holding.generic.admin.autorisation.function_role.RoleRepository;
import dwe.holding.generic.admin.autorisation.function_role.UserRoleRepository;
import dwe.holding.generic.admin.autorisation.member.LocalMemberRepository;
import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.model.*;
import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MigrationAdminService {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localMemberRepository;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;

    public MigrationAdminService(MemberRepository memberRepository, LocalMemberRepository localMemberRepository, UserRepository userRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository) {
        this.memberRepository = memberRepository;
        this.localMemberRepository = localMemberRepository;
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public void init() {
        if (memberRepository.findAll().isEmpty()) {
            String password = passwordEncoder.encode("pas");

            Member member = memberRepository.saveAndFlush(
                    Member.builder()
                            .name("DWE Holding")
                            .active(YesNoEnum.Yes)
                            .password(password)
                            .start(LocalDate.now())
                            .stop(LocalDate.now())
                            .shortCode("DWE")
                            .simultaneousUsers(4)
                            .build()
            );
            LocalMember localMember = localMemberRepository.saveAndFlush(
                    LocalMember.builder().localMemberName("Local member").mid(member.getId()).member(member).build()
            );

            User user = userRepository.saveAndFlush(
                    User.builder()
                            .name("daniel")
                            .email("danielweijers@gmail.com")
                            .account("daan")
                            .changePassword(true)
                            .password(password)
                            .language(LanguagePrefEnum.English)
                            .personnelStatus(PersonnelStatusEnum.Vet)
                            .loginEnabled(YesNoEnum.Yes)
                            .member(member)
                            .build()
            );

            List<Function> listFunc = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("member_READ").build(),
                            Function.builder().name("localmember_READ").build(),
                            Function.builder().name("function_READ").build(),
                            Function.builder().name("role_READ").build(),
                            Function.builder().name("user_READ").build(),
                            Function.builder().name("resetpassword_READ").build(),
                            Function.builder().name("userpreferences_READ").build(),
                            Function.builder().name("member_CREATE").build(),
                            Function.builder().name("localmember_CREATE").build(),
                            Function.builder().name("function_CREATE").build(),
                            Function.builder().name("role_CREATE").build(),
                            Function.builder().name("user_CREATE").build(),
                            Function.builder().name("resetpassword_CREATE").build(),
                            Function.builder().name("userpreferences_CREATE").build()
                            // distribution
                            // suppliesandinventory
                    )
            );
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("super_admin").memberId(member.getId()).build(),
                            Role.builder().name("admin").memberId(member.getId()).build()
                    )
            );
            Role role = listRole.getLast();

            List<FunctionRole> funcRole = functionRoleRepository.saveAllAndFlush(
                    listFunc.stream()
                            .map(func -> {
                                return (FunctionRole) FunctionRole.builder().function(func).role(role).build();
                            })
                            .toList()
            );
            userRoleRepository.saveAndFlush(
                    UserRole.builder().role(role).user(user).build()
            );
        }
    }
}