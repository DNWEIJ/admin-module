package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.authorisation.IPNumber;
import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.model.tenant.IPSecurity;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserRole;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping("/admin")
@AllArgsConstructor
public class UserController {
    public static final int FOUR = 4;

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;


    @GetMapping("/users")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("users", userRepository.findAll());
        return "admin-module/user/list";
    }

    @GetMapping("/user")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new User());
        return "admin-module/user/action";

    }

    @GetMapping("/user/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, userRepository.findById(id).orElseThrow());
        return "admin-module/user/action";
    }

    @PostMapping("/user")
    String save(User user, @RequestParam(required = false) List<Long> checkedRoles, RedirectAttributes redirect) {
        if (checkedRoles == null) checkedRoles = new ArrayList<>();

        userRoleService.processUserAdd(checkedRoles, user);
        userRoleService.processUserDelete(checkedRoles, user);
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/admin/users";
    }

    @PostMapping("/user/{userId}/ipnumber")
    String saveNewIp(@PathVariable Long userId, @NotNull Integer numberOne, @NotNull Integer numberTwo, @NotNull Integer numberThree, @NotNull Integer numberFour, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        IPNumber ipnumber = new IPNumber(numberOne, numberTwo, numberThree, numberFour);
        user.getIpNumbers().add(
                IPSecurity.builder().userId(user.getId()).ipnumber(ipnumber.toString()).build()
        );
        model
                .addAttribute("user", userRepository.save(user))
                .addAttribute("ipNumberParts", new IPNumber());
        return "admin-module/user/fragments/fragments::bodyTableIpNumbers";
    }

    @DeleteMapping("/user/{userId}/ipnumber/{ipnumber}")
    String deleteIp(@PathVariable Long userId, @PathVariable Long ipnumber, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        user.getIpNumbers().remove(
                user.getIpNumbers().stream().filter(ip -> ip.getId().equals(ipnumber)).findFirst().orElseThrow()
        );
        model
                .addAttribute("user", userRepository.save(user))
                .addAttribute("ipNumberParts", new IPNumber());
        userRepository.save(user);
        return "admin-module/user/fragments/fragments::bodyTableIpNumbers";
    }


    private void setModelData(Model model, User user) {
        model.addAttribute("user", user)
                .addAttribute("languagePrefList", LanguagePrefEnum.getWebList())
                .addAttribute("personnelStatusList", PersonnelStatusEnum.getWebList())
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                // TODO should we not validate here on SUPER_ADMIN and then just add 1 member in the dropdown?
                .addAttribute("membersList", memberRepository.findAllProjectedBy()
                        .stream()
                        .map(member -> new PresentationElement(member.id(), member.name(), false))
                        .toList())
                .addAttribute("roles", getAllRolesAndCheckIfActive(user.getUserRoles()))
                .addAttribute("ipNumberParts", new IPNumber());

        if (AutorisationUtils.hasRole("SUPER_ADMIN")) {
            model.addAttribute("isSuperAdmin", true);
        } else {
            model.addAttribute("isSuperAdmin", false);
        }
    }

    private List<List<PresentationElement>> getAllRolesAndCheckIfActive(Set<UserRole> userRoles) {
        if (userRoles == null) userRoles = new HashSet<>();

        Map<Long, String> roleIdsChecked = userRoles.stream().collect(
                Collectors.toMap(s -> s.getRole().getId(), s -> s.getRole().getName())
        );

        List<PresentationElement> list = new ArrayList<>(roleRepository.findAll().stream()
                .map(f -> new PresentationElement(f.getId(), f.getName(), roleIdsChecked.containsKey(f.getId())))
                .sorted(Comparator.comparing(PresentationElement::getName)).toList());

        List<List<PresentationElement>> groups = new ArrayList<>();
        for (int i = 0; i < list.size(); i += FOUR) {
            groups.add(list.subList(i, Math.min(i + FOUR, list.size())));
        }
        return groups;
    }
}