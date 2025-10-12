package dwe.holding.generic.cartracker.setup;

import dwe.holding.generic.admin.authorisation.function_role.FunctionRepository;
import dwe.holding.generic.admin.authorisation.function_role.FunctionRoleRepository;
import dwe.holding.generic.admin.authorisation.function_role.RoleRepository;
import dwe.holding.generic.admin.authorisation.function_role.UserRoleRepository;
import dwe.holding.generic.admin.authorisation.member.LocalMemberRepository;
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

@Service
@Slf4j
public class SetupCarTrackerService {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final LocalMemberRepository localMemberRepository;

    public SetupCarTrackerService(MemberRepository memberRepository, UserRepository userRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository, LocalMemberRepository localMemberRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
        this.userRoleRepository = userRoleRepository;
        this.localMemberRepository = localMemberRepository;
    }


    @Transactional
    public void init() {

        if (memberRepository.findAll().stream().filter(member -> member.getShortCode().equalsIgnoreCase("CAR")).findFirst().isEmpty()) {
            log.info("SetupCarTrackerService:: member");
            String password = passwordEncoder.encode("carTracker!");

            Member member = memberRepository.saveAndFlush(
                    Member.builder()
                            .name("Car Tracker")
                            .localMemberSelectRequired(YesNoEnum.No)
                            .active(YesNoEnum.Yes)
                            .password(password)
                            .start(LocalDate.now())
                            .stop(LocalDate.now())
                            .shortCode("CAR")
                            .simultaneousUsers(1)
                            .applicationName("cartracker")
                            .applicationView("cartracker-module")
                            .applicationRedirect("redirect:/cartracker/trip")
                            .build()
            );

            LocalMember localMember = localMemberRepository.saveAndFlush(
                    LocalMember.builder()
                            .localMemberName("CAR")
                            .mid(member.getId())
                            .member(member).build()
            );
            log.info("SetupCarTrackerService:: user");
            User daniel = userRepository.saveAndFlush(
                    User.builder()
                            .name("daniel")
                            .email("danielweijers@gmail.com")
                            .account("daniel")
                            .changePassword(false)
                            .password(passwordEncoder.encode("Daniel0904!"))
                            .language(LanguagePrefEnum.English)
                            .personnelStatus(PersonnelStatusEnum.Other)
                            .loginEnabled(YesNoEnum.Yes)
                            .member(member)
                            .memberLocalId(localMember.getId())
                            .build()
            );
            User maria = userRepository.saveAndFlush(
                    User.builder()
                            .name("Maria")
                            .email("maria.krebbers@gmail.com")
                            .account("Maria")
                            .changePassword(false)
                            .password(passwordEncoder.encode("drivingSporty!"))
                            .language(LanguagePrefEnum.English)
                            .personnelStatus(PersonnelStatusEnum.Other)
                            .loginEnabled(YesNoEnum.Yes)
                            .member(member)
                            .memberLocalId(localMember.getId())
                            .build()
            );
            User suus = userRepository.saveAndFlush(
                    User.builder()
                            .name("Suzanne")
                            .email("suzanne.weijers@gmail.com")
                            .account("Suzanne")
                            .changePassword(false)
                            .password(passwordEncoder.encode("drivingWithoutPetrol!"))
                            .language(LanguagePrefEnum.English)
                            .personnelStatus(PersonnelStatusEnum.Other)
                            .loginEnabled(YesNoEnum.Yes)
                            .member(member)
                            .memberLocalId(localMember.getId())
                            .build()
            );
            Role defaultRole = roleRepository.getRoleByName("DEFAULT");
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(defaultRole).user(suus).build(),
                            UserRole.builder().role(defaultRole).user(maria).build(),
                            UserRole.builder().role(defaultRole).user(daniel).build()
                    )
            );

            log.info("SetupCarTrackerService:: general functions for CAR_USER role");
            List<Function> listFuncDefault = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("TRIP_READ").build(),
                            Function.builder().name("TRIP_CREATE").build(),
                            Function.builder().name("TANK_READ").build(),
                            Function.builder().name("ALLUSER_READ").build(),
                            Function.builder().name("ALL_READ").build()
                    ));


            log.info("SetupAdminService:: role");
            Role userRole = roleRepository.saveAndFlush(
                    Role.builder().name("CAR_USER").memberId(member.getId()).build()

            );
            log.info("SetupCarTrackerService:: start creating the connection between function and role..");
            functionRoleRepository.saveAllAndFlush(
                    listFuncDefault.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().function(func).role(userRole).build())
                            .toList()
            );

            log.info("SetupCarTrackerService:: CONNECT USER TO THE ROLE");
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(userRole).user(maria).build(),
                            UserRole.builder().role(userRole).user(daniel).build(),
                            UserRole.builder().role(userRole).user(suus).build()
                    )
            );
        }
    }
}