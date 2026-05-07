package dwe.holding.admin.setup;

import dwe.holding.admin.authorisation.notenant.function.FunctionRepository;
import dwe.holding.admin.authorisation.notenant.function_role.InternalFunctionRoleRepository;
import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.authorisation.tenant.user.UserNoMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRoleRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserNoMember;
import dwe.holding.admin.model.tenant.UserRole;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class SetupAdminService {
    private final MemberRepository memberRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final InternalFunctionRoleRepository functionRoleRepository;
    private final UserNoMemberRepository userNoMemberRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;


    @Transactional
    public void updateDaniel() {

        UserNoMember userNoMember = userNoMemberRepository.findByAccount("daniel").stream().filter(usr -> usr.getMemberId().equals(77L)).findFirst().orElseThrow();
        log.info("MigrationAdminService:: CONNECT USER TO THE ROLE");
        User user = userRepository.findById(userNoMember.getId()).orElseThrow();
        roleRepository.getRoleByName("SUPER_ADMIN");

        userRoleRepository.saveAllAndFlush(
                List.of(
                        UserRole.builder().role(roleRepository.getRoleByName("SUPER_ADMIN")).user(user).build(),
                        UserRole.builder().role(roleRepository.getRoleByName("ADMIN_READ")).user(user).build(),
                        UserRole.builder().role(roleRepository.getRoleByName("ADMIN_CREATE")).user(user).build(),
                        UserRole.builder().role(roleRepository.getRoleByName("DEFAULT")).user(user).build()
                )
        );

    }

    @Transactional()
    public void importRolesAndConenctToDaniel() {
        log.info("importRolesAndConenctToDaniel:: role");
        List<Role> listRole = roleRepository.saveAllAndFlush(
                List.of(
                        Role.builder().name("SUPER_ADMIN").memberId(77L).build(),
                        Role.builder().name("ADMIN_READ").memberId(77L).build(),
                        Role.builder().name("ADMIN_CREATE").memberId(77L).build(),
                        Role.builder().name("DEFAULT").memberId(77L).build()
                )
        );
        Role roleSuperAdmin = listRole.stream().filter(r -> r.getName().equals("SUPER_ADMIN")).findFirst().get();
        Role roleAdminCreate = listRole.stream().filter(r -> r.getName().equals("ADMIN_CREATE")).findFirst().get();
        Role roleAdminRead = listRole.stream().filter(r -> r.getName().equals("ADMIN_READ")).findFirst().get();
        Role roleDefault = listRole.stream().filter(r -> r.getName().equals("DEFAULT")).findFirst().get();

        UserNoMember userNoMember = userNoMemberRepository.findByAccount("daniel").stream().filter(usr -> usr.getMemberId().equals(77L)).findFirst().orElseThrow();
        User user = userRepository.findById(userNoMember.getId()).orElseThrow();

        userRoleRepository.saveAllAndFlush(
                List.of(
                        UserRole.builder().role(roleSuperAdmin).user(user).build(),
                        UserRole.builder().role(roleAdminCreate).user(user).build(),
                        UserRole.builder().role(roleAdminRead).user(user).build(),
                        UserRole.builder().role(roleDefault).user(user).build()
                )
        );
    }

    @Transactional
    public Long init() {
        if (functionRepository.findAll().isEmpty() || memberRepository.findAll().isEmpty()) {
//            log.info("MigrationAdminService:: member");
//            String password = passwordEncoder.encode("pas!");
//
//            Member member = memberRepository.saveAndFlush(
//                    Member.builder()
//                            .name("DWE Holding")
//                            .localMemberSelectRequired(YesNoEnum.No)
//                            .active(YesNoEnum.Yes)
//                            .password(password)
//                            .startDate(LocalDate.now())
//                            .stopDate(LocalDate.now())
//                            .shortCode("DWE")
//                            .simultaneousUsers(4)
//                            .applicationName("admin")
//                            .applicationView("admin-module")
//                            .applicationRedirect("/admin-module/index")
//                            .build()
//            );
//            log.info("MigrationAdminService:: user");
//            User user = userRepository.saveAndFlush(
//                    User.builder()
//                            .name("daniel")
//                            .email("danielweijers@gmail.com")
//                            .account("daniel")
//                            .changePassword(true)
//                            .password(password)
//                            .language(LanguagePrefEnum.English)
//                            .personnelStatus(PersonnelStatusEnum.Vet)
//                            .loginEnabled(YesNoEnum.Yes)
//                            .member(member)
//                            .build()
//            );

            log.info("MigrationAdminService:: function for SUPER_ADMIN Role");
            List<Function> listFuncSuperAdmin = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("MEMBER_READ").build(),
                            Function.builder().name("MEMBER_CREATE").build()
                    )
            );

            log.info("MigrationAdminService:: function for ADMIN Role");
            List<Function> listFuncAdmin = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("LOCALMEMBER_READ").build(),
                            Function.builder().name("FUNCTION_READ").build(),
                            Function.builder().name("ROLE_READ").build(),
                            Function.builder().name("USER_READ").build(),

                            Function.builder().name("LOCALMEMBER_CREATE").build(),
                            Function.builder().name("FUNCTION_CREATE").build(),
                            Function.builder().name("ROLE_CREATE").build(),
                            Function.builder().name("USER_CREATE").build()

                    )
            );
            log.info("MigrationAdminService:: general functions for DEFAULT role");
            List<Function> listFuncDefault = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("RESETPASSWORD_READ").build(),
                            Function.builder().name("USERPREFERENCES_READ").build(),
                            Function.builder().name("INDEX_READ").build(),
                            Function.builder().name("SETLOCALMEMBER_READ").build(),
                            Function.builder().name("LOGOUT_READ").build(),
                            Function.builder().name("ERROR_READ").build(),

                            Function.builder().name("USERPREFERENCES_CREATE").build(),
                            Function.builder().name("RESETPASSWORD_CREATE").build(),
                            Function.builder().name("SETLOCALMEMBER_CREATE").build()
                    ));


            log.info("MigrationAdminService:: role");
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("SUPER_ADMIN").memberId(77L).build(),
                            Role.builder().name("ADMIN_READ").memberId(77L).build(),
                            Role.builder().name("ADMIN_CREATE").memberId(77L).build(),
                            Role.builder().name("DEFAULT").memberId(77L).build()
                    )
            );
            log.info("MigrationAdminService:: start creating the connection between function and role..");
            Role roleSuperAdmin = listRole.stream().filter(r -> r.getName().equals("SUPER_ADMIN")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncSuperAdmin.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleSuperAdmin.getId()).memberId(77L).build())
                            .toList()
            );

            Role roleAdminCreate = listRole.stream().filter(r -> r.getName().equals("ADMIN_CREATE")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncAdmin.stream().filter(f -> f.getName().contains("CREATE"))
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleAdminCreate.getId()).memberId(77L).build())
                            .toList()
            );
            Role roleAdminRead = listRole.stream().filter(r -> r.getName().equals("ADMIN_READ")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncAdmin.stream().filter(f -> f.getName().contains("READ"))
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleAdminRead.getId()).memberId(77L).build())
                            .toList()
            );

            Role roleDefault = listRole.stream().filter(r -> r.getName().equals("DEFAULT")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncDefault.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleDefault.getId()).memberId(77L).build())
                            .toList()
            );

            UserNoMember userNoMember = userNoMemberRepository.findByAccount("daniel").stream().filter(usr -> usr.getMemberId().equals(77L)).findFirst().orElseThrow();
            log.info("MigrationAdminService:: CONNECT USER TO THE ROLE");
            User user = userRepository.findById(userNoMember.getId()).orElseThrow();

            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(roleSuperAdmin).user(user).build(),
                            UserRole.builder().role(roleAdminCreate).user(user).build(),
                            UserRole.builder().role(roleAdminRead).user(user).build(),
                            UserRole.builder().role(roleDefault).user(user).build()
                    )
            );
            return 77L;
        }
        return null;
    }

    public void adminFunctionRoleAndConnect(Long memberId) {
        log.info("MigrationAdminService:: function for SUPER_ADMIN Role");
        List<Function> listFuncSuperAdmin = functionRepository.saveAllAndFlush(
                List.of(
                        Function.builder().name("MEMBER_READ").build(),
                        Function.builder().name("MEMBER_CREATE").build()
                )
        );

        log.info("MigrationAdminService:: function for ADMIN Role");
        List<Function> listFuncAdmin = functionRepository.saveAllAndFlush(
                List.of(
                        Function.builder().name("LOCALMEMBER_READ").build(),
                        Function.builder().name("FUNCTION_READ").build(),
                        Function.builder().name("ROLE_READ").build(),
                        Function.builder().name("USER_READ").build(),

                        Function.builder().name("LOCALMEMBER_CREATE").build(),
                        Function.builder().name("FUNCTION_CREATE").build(),
                        Function.builder().name("ROLE_CREATE").build(),
                        Function.builder().name("USER_CREATE").build()

                )
        );
        log.info("MigrationAdminService:: general functions for DEFAULT role");
        List<Function> listFuncDefault = functionRepository.saveAllAndFlush(
                List.of(
                        Function.builder().name("RESETPASSWORD_READ").build(),
                        Function.builder().name("USERPREFERENCES_READ").build(),
                        Function.builder().name("INDEX_READ").build(),
                        Function.builder().name("SETLOCALMEMBER_READ").build(),
                        Function.builder().name("LOGOUT_READ").build(),

                        Function.builder().name("USERPREFERENCES_CREATE").build(),
                        Function.builder().name("RESETPASSWORD_CREATE").build(),
                        Function.builder().name("SETLOCALMEMBER_CREATE").build()
                ));


        log.info("MigrationAdminService:: role");
        List<Role> listRole = roleRepository.saveAllAndFlush(
                List.of(
                        Role.builder().name("SUPER_ADMIN").memberId(memberId).build(),
                        Role.builder().name("ADMIN_READ").memberId(memberId).build(),
                        Role.builder().name("ADMIN_CREATE").memberId(memberId).build(),
                        Role.builder().name("DEFAULT").memberId(memberId).build()
                )
        );
        log.info("MigrationAdminService:: start creating the connection between function and role..");
        Role roleSuperAdmin = listRole.stream().filter(r -> r.getName().equals("SUPER_ADMIN")).findFirst().get();
        functionRoleRepository.saveAllAndFlush(
                listFuncSuperAdmin.stream()
                        .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleSuperAdmin.getId()).memberId(77L).build())
                        .toList()
        );

        Role roleAdminCreate = listRole.stream().filter(r -> r.getName().equals("ADMIN_CREATE")).findFirst().get();
        functionRoleRepository.saveAllAndFlush(
                listFuncAdmin.stream().filter(f -> f.getName().contains("CREATE"))
                        .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleAdminCreate.getId()).memberId(77L).build())
                        .toList()
        );
        Role roleAdminRead = listRole.stream().filter(r -> r.getName().equals("ADMIN_READ")).findFirst().get();
        functionRoleRepository.saveAllAndFlush(
                listFuncAdmin.stream().filter(f -> f.getName().contains("READ"))
                        .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleAdminRead.getId()).memberId(77L).build())
                        .toList()
        );

        Role roleDefault = listRole.stream().filter(r -> r.getName().equals("DEFAULT")).findFirst().get();
        functionRoleRepository.saveAllAndFlush(
                listFuncDefault.stream()
                        .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleDefault.getId()).memberId(77L).build())
                        .toList()
        );

    }
}