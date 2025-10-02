package dwe.holding.generic.app.teammover.setup;

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
public class SetupTeamMoverAdminService {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localMemberRepository;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;

    public SetupTeamMoverAdminService(MemberRepository memberRepository, LocalMemberRepository localMemberRepository, UserRepository userRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository) {
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
                            .applicationName("TeamMover")
                            .applicationView("teammover-module")
                            .applicationRedirect("redirect:/teammover/game/list")
                            .build()
            );
            log.info("MigrationTeamMoverAdminService:: localMember: the teams");
            List<LocalMember> localMembers = localMemberRepository.saveAllAndFlush(
                    List.of(
                            LocalMember.builder().localMemberName("GO12-2").mid(member.getId()).member(member).build(),
                            LocalMember.builder().localMemberName("GO12-1").mid(member.getId()).member(member).build()
                    )
            );

            member.getLocalMembers().addAll(localMembers);
            memberRepository.saveAndFlush(member);
            log.info("MigrationTeamMoverAdminService:: function");
            List<Function> listFunc = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("GAME_READ").build(),
                            Function.builder().name("DRIVER_READ").build(),
                            Function.builder().name("GAME_CREATE").build(),
                            Function.builder().name("DRIVER_CREATE").build()
                    )
            );

            log.info("MigrationTeamMoverAdminService:: role");
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("TEAM_MOVER").memberId(member.getId()).build(),
                            Role.builder().name("PLANNER").memberId(member.getId()).build()

                    )
            );
            Role plannerRole = listRole.stream().filter(r -> r.getName().equals("PLANNER")).findFirst().get();
            Role teammoverRole = listRole.stream().filter(r -> r.getName().equals("TEAM_MOVER")).findFirst().get();

            log.info("MigrationTeamMoverAdminService:: function-role");
            functionRoleRepository.saveAllAndFlush(
                    listFunc.stream()
                            .map(func -> {
                                if (func.getName().equalsIgnoreCase("GAME_CREATE")) {
                                    return FunctionRole.builder().function(func).role(plannerRole).build();
                                }
                                return FunctionRole.builder().function(func).role(teammoverRole).build();
                            })
                            .toList()
            );

            log.info("MigrationTeamMoverAdminService:: user");
            record UserInfo(String name, String account, String email) {
            }

            List<UserInfo> userData = List.of(
                    new UserInfo("Jeroen Peters", "jeroen", "donotreply@zvs.nl"),
                    new UserInfo("Arjan Buijsse", "arjan", "donotreply@zvs.nl"),
                    new UserInfo("Paul Geurds", "paul", "donotreply@zvs.nl"),
                    new UserInfo("Kim Broeders", "kim", "donotreply@zvs.nl"),
                    new UserInfo("Arnaud", "arnoud", "donotreply@zvs.nl"),
                    new UserInfo("Joesephine", "josephine", "donotreply@zvs.nl"),
                    new UserInfo("Roosmarijn Roelevink", "roosmarijn", "donotreply@zvs.nl"),
                    new UserInfo("Vera Kortekaas", "vera", "donotreply@zvs.nl")
            );

            User baseUser = User.builder()
                    .changePassword(true)
                    .password(password)
                    .language(LanguagePrefEnum.Dutch)
                    .personnelStatus(PersonnelStatusEnum.Vet)
                    .loginEnabled(YesNoEnum.Yes)
                    .member(member)
                    .build();

            List<User> userSavedList =
                    userRepository.saveAllAndFlush(
                            userData.stream()
                                    .map(info -> (User) baseUser.toBuilder()
                                            .name(info.name())
                                            .account(info.account())
                                            .email(info.email())
                                            .build()
                                    ).toList()
                    );


            log.info("MigrationTeamMoverAdminService:: user-role");
            userRoleRepository.saveAllAndFlush(
                    userSavedList.stream().map(user ->
                            UserRole.builder().role(teammoverRole).user(user).build()).toList()
            );
            userRoleRepository.saveAllAndFlush(
                    userSavedList.stream().filter(user -> user.getAccount().equalsIgnoreCase("jeroen") ||
                            user.getAccount().equalsIgnoreCase("arjan") ||
                            user.getAccount().equalsIgnoreCase("daniel")
                    ).map(user ->UserRole.builder().role(plannerRole).user(user).build()).toList()
            );

            Role defaultRole = roleRepository.getRoleByName("DEFAULT");
            userRoleRepository.saveAllAndFlush(
                    userSavedList.stream().map(user ->
                            UserRole.builder().role(defaultRole).user(user).build()).toList()
            );

            // *********** //
            // ADMIN STAFF //
            // *********** //
            User daniel = userRepository.saveAndFlush(
                    User.builder()
                            .name("daniel")
                            .email("danielweijers@gmail.com")
                            .account("daniel")
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
                            UserRole.builder().role(plannerRole).user(daniel).build(),
                            UserRole.builder().role(teammoverRole).user(daniel).build(),
                            UserRole.builder().role(defaultRole).user(daniel).build()
                    )
            );
        }
    }
}