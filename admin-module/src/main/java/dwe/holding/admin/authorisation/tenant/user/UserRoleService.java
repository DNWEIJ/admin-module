package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.authorisation.tenant.user.mapper.UserMapper;
import dwe.holding.admin.model.tenant.Role;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserRole;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@Slf4j
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;

    @Transactional
    public void processUserAdd(List<Long> checked, User formUser) {
        User user = new User();
        if (formUser.isNew()) {
            userMapper.updateUserFromForm(formUser, user);
            user.setPassword(AutorisationUtils.getCurrentMemberPassword());
            user.setMember(AutorisationUtils.getCurrentMember());
            user.setChangePassword(true);

            user = userRepository.save(user);
        } else {
            user = userRepository.findById(formUser.getId()).orElseThrow();
            user.setAccount(formUser.getAccount());
            user.setName(formUser.getName());
            user.setEmail(formUser.getEmail());
            user.setLanguage(formUser.getLanguage());
            user.setPersonnelStatus(formUser.getPersonnelStatus());
            user.setLoginEnabled(formUser.getLoginEnabled());
            // TODO set member for superAdmin

            user = userRepository.save(user);
        }

        // user is finished, now do the roles
        List<Long> currentUserRoleIdsAdd = new ArrayList<>(user.getUserRoles().stream().map(a -> a.getRole().getId()).toList());

        final User processUser = user;
        // add records
        List<Long> addRecords = checked.stream().filter(id -> !currentUserRoleIdsAdd.contains(id)).toList();
        addRecords.forEach(id -> {
                    Role role = roleRepository.findById(id).orElseThrow();
                    userRoleRepository.save(new UserRole(processUser, role));
                }
        );
        userRepository.flush();
    }

    @Transactional
    public void processUserDelete(List<Long> checked, User formUser) {
        User userDelete = userRepository.findById(formUser.getId()).orElseThrow();

        userDelete.getUserRoles().removeIf(userRole -> {
            if (!checked.contains(userRole.getRole().getId())) {
                log.info("delete");
                return true;
            }
            return false;
        });

        userRepository.save(userDelete);
        userRepository.flush();
    }
}
