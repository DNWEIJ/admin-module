package dwe.holding.generic.admin.setup;

import dwe.holding.generic.admin.authorisation.function_role.FunctionRepository;
import dwe.holding.generic.admin.authorisation.function_role.FunctionRoleRepository;
import dwe.holding.generic.admin.authorisation.function_role.RoleRepository;
import dwe.holding.generic.admin.authorisation.function_role.UserRoleRepository;
import dwe.holding.generic.admin.authorisation.member.MemberRepository;
import dwe.holding.generic.admin.authorisation.user.UserRepository;
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
import java.util.UUID;

@Service
@Slf4j
public class SetupAdminService {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;

    public SetupAdminService(MemberRepository memberRepository, UserRepository userRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public UUID init() {

        if (memberRepository.findAll().isEmpty()) {
            log.info("MigrationAdminService:: member");
            String password = passwordEncoder.encode("pas!");

            Member member = memberRepository.saveAndFlush(
                    Member.builder()
                            .name("DWE Holding")
                            .localMemberSelectRequired(YesNoEnum.No)
                            .active(YesNoEnum.Yes)
                            .password(password)
                            .start(LocalDate.now())
                            .stop(LocalDate.now())
                            .shortCode("DWE")
                            .simultaneousUsers(4)
                            .applicationName("admin")
                            .applicationView("admin-module")
                            .applicationRedirect("redirect:/admin/index")
                            .build()
            );
            log.info("MigrationAdminService:: user");
            User user = userRepository.saveAndFlush(
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
                            Function.builder().name("USERPREFERENCES_CREATE").build(),
                            Function.builder().name("RESETPASSWORD_CREATE").build(),
                            Function.builder().name("INDEX_READ").build(),
                            Function.builder().name("SETLOCALMEMBER_READ").build(),
                            Function.builder().name("SETLOCALMEMBER_CREATE").build(),
                            Function.builder().name("LOGOUT_READ").build()

                    ));


            log.info("MigrationAdminService:: role");
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("SUPER_ADMIN").memberId(member.getId()).build(),
                            Role.builder().name("ADMIN").memberId(member.getId()).build(),
                            Role.builder().name("DEFAULT").memberId(member.getId()).build()
                    )
            );
            log.info("MigrationAdminService:: start creating the connection between function and role..");
            Role roleSuperAdmin = listRole.stream().filter(r -> r.getName().equals("SUPER_ADMIN")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncSuperAdmin.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().function(func).role(roleSuperAdmin).build())
                            .toList()
            );
            Role roleAdmin = listRole.stream().filter(r -> r.getName().equals("ADMIN")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncAdmin.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().function(func).role(roleAdmin).build())
                            .toList()
            );
            Role roleDefault = listRole.stream().filter(r -> r.getName().equals("DEFAULT")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncDefault.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().function(func).role(roleDefault).build())
                            .toList()
            );

            log.info("MigrationAdminService:: CONNECT USER TO THE ROLE");
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(roleSuperAdmin).user(user).build(),
                            UserRole.builder().role(roleAdmin).user(user).build(),
                            UserRole.builder().role(roleDefault).user(user).build()
                    )
            );
            return member.getId();
        }
        return null;
    }
}