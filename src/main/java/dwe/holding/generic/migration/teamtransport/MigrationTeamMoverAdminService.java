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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
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
        if (memberRepository.findAll().stream().filter(mem -> mem.getShortCode().equals("ZVS")).toList().isEmpty()) {
            String password = passwordEncoder.encode("ZVS!DeEerste!");
            log.info("MigrationTeamMoverAdminService:: member");
            Member member = memberRepository.saveAndFlush(
                    Member.builder()
                            .name("ZVS Sassenheim")
                            .localMemberSelectRequired(YesNoEnum.Yes)
                            .active(YesNoEnum.Yes)
                            .password(password)
                            .start(LocalDate.now())
                            .stop(LocalDate.now())
                            .shortCode("ZVS")
                            .simultaneousUsers(40)
                            .build()
            );
            log.info("MigrationTeamMoverAdminService:: localMember");
            List<LocalMember> localMembers = localMemberRepository.saveAllAndFlush(
                    List.of(
                            LocalMember.builder().localMemberName("ZVS GO12-2").mid(member.getId()).member(member).build(),
                            LocalMember.builder().localMemberName("ZVS GO14-1").mid(member.getId()).member(member).build()
                    )
            );

            member.getLocalMembers().addAll(localMembers);
            memberRepository.saveAndFlush(member);
            log.info("MigrationTeamMoverAdminService:: function");
            List<Function> listFunc = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("game_READ").build(),
                            Function.builder().name("driver_READ").build(),
                            Function.builder().name("game_CREATE").build(),
                            Function.builder().name("driver_CREATE").build()
                    )
            );
            log.info("MigrationTeamMoverAdminService:: role");
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("team-mover").memberId(member.getId()).build(),
                            Role.builder().name("planner").memberId(member.getId()).build()

                    )
            );
            Role planner = listRole.stream().filter(r -> r.getName().equals("planner")).findFirst().get();
            Role teammover = listRole.stream().filter(r -> r.getName().equals("team-mover")).findFirst().get();

            log.info("MigrationTeamMoverAdminService:: function-role");
            List<FunctionRole> funcRole = functionRoleRepository.saveAllAndFlush(
                    listFunc.stream()
                            .map(func -> {
                                if (func.getName().toLowerCase().contains("game")) {
                                    return FunctionRole.builder().function(func).role(planner).build();
                                }
                                return FunctionRole.builder().function(func).role(teammover).build();
                            })
                            .toList()
            );
            log.info("MigrationTeamMoverAdminService:: user");
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
            log.info("MigrationTeamMoverAdminService:: user-role");
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(planner).user(jeroen).build(),
                            UserRole.builder().role(teammover).user(jeroen).build()
                    )
            );


            // *********** //
            // ADMIN STAFF //
            // *********** //
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
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(planner).user(daniel).build(),
                            UserRole.builder().role(teammover).user(daniel).build()
                    )
            );
        }
    }
}