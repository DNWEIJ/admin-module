package dwe.holding.generic.migration.teamtransport;

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
public class MigrationTeamMoverAdminService {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localMemberRepository;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;

    public MigrationTeamMoverAdminService(MemberRepository memberRepository, LocalMemberRepository localMemberRepository, UserRepository userRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository) {
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
            String password = passwordEncoder.encode("ZVS!DeEerste!");

            Member member = memberRepository.saveAndFlush(
                    Member.builder()
                            .name("ZVS Sassenheim")
                            .active(YesNoEnum.Yes)
                            .password(password)
                            .start(LocalDate.now())
                            .stop(LocalDate.now())
                            .shortCode("ZVS")
                            .simultaneousUsers(40)
                            .build()
            );
            LocalMember localMember = localMemberRepository.saveAndFlush(
                    LocalMember.builder().localMemberName("ZVS Sassenheim").mid(member.getId()).member(member).build()
            );

            User jeroen = userRepository.saveAndFlush(
                    User.builder()
                            .name("Jeroen Peters")
                            .email("JeroenPeters@zvs.nl")
                            .account("jeroen")
                            .changePassword(true)
                            .password(password)
                            .language(LanguagePrefEnum.Dutch)
                            .personnelStatus(PersonnelStatusEnum.Vet)
                            .loginEnabled(YesNoEnum.Yes)
                            .member(member)
                            .build()
            );


            List<Function> functionForUser = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("resetpassword_READ").build(),
                            Function.builder().name("userpreferences_READ").build(),
                            Function.builder().name("resetpassword_CREATE").build(),
                            Function.builder().name("userpreferences_CREATE").build()

                    )
            );

            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("super_admin").memberId(member.getId()).build(),
                            Role.builder().name("planner").memberId(member.getId()).build()
                    )
            );
            Role planner = listRole.stream().filter(r -> r.getName().equals("planner")).findFirst().get();

            List<FunctionRole> funcRole = functionRoleRepository.saveAllAndFlush(
                    functionForUser.stream()
                            .map(func -> {
                                return (FunctionRole) FunctionRole.builder().function(func).role(planner).build();
                            })
                            .toList()
            );
            userRoleRepository.saveAndFlush(
                    UserRole.builder().role(planner).user(jeroen).build()
            );


            // *********** //
            // ADMIN STAFF //
            // *********** //
            Role superAdminRole = listRole.stream().filter(r -> r.getName().equals("super_admin")).findFirst().get();
            User daniel = userRepository.saveAndFlush(
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

            List<Function> SuperAdmin = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("member_READ").build(),
                            Function.builder().name("localmember_READ").build(),
                            Function.builder().name("function_READ").build(),
                            Function.builder().name("role_READ").build(),
                            Function.builder().name("user_READ").build(),
                            Function.builder().name("member_CREATE").build(),
                            Function.builder().name("localmember_CREATE").build(),
                            Function.builder().name("function_CREATE").build(),
                            Function.builder().name("role_CREATE").build(),
                            Function.builder().name("user_CREATE").build()
                    )
            );
        }
    }
}