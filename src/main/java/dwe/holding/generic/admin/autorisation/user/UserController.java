package dwe.holding.generic.admin.autorisation.user;

import dwe.holding.generic.admin.autorisation.function_role.RoleRepository;
import dwe.holding.generic.admin.autorisation.function_role.UserRoleRepository;
import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.PresentationFunction;
import dwe.holding.generic.admin.model.Role;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserRole;
import dwe.holding.generic.admin.model.base.BaseBO;
import dwe.holding.generic.admin.model.base.ToString;
import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.admin.security.AutorisationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;


@Controller
@Validated
public class UserController {
    public static final int FOUR = 4;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final SimpleGrantedAuthority SUPER_ADMIN = new SimpleGrantedAuthority("SUPER_ADMIN");


    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, MemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/admin/user")
    String save(@Valid Form form, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/user/action";
        }
        UUID userId = processUser(form.checkedFunctions, form.user);
        redirect.addFlashAttribute("message", "Role saved successfully!");
        return getRedirectFor(request, userId, "redirect:/user");
    }

    @GetMapping("/admin/user")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new User());
        return "admin-module/user/action";

    }

    @GetMapping("/admin/user/{id}")
    String showEditScreen(@PathVariable @NotNull UUID id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, userRepository.findById(id).orElseThrow());
        return "admin-module/user/action";
    }

    @GetMapping("/admin/user/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("users", userRepository.findAll());
        return "admin-module/user/list";
    }

    private List<List<PresentationFunction>> getAllFunctionsAndCheckedIfActive(Set<UserRole> userRoles) {
        if (userRoles == null ) return new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        Map<UUID, String> roleIdsChecked = userRoles.stream().collect(
                Collectors.toMap(s -> s.getRole().getId(), s -> s.getRole().getName())
        );

        List<PresentationFunction> list = new ArrayList<>(roles.stream()
                .map(f -> new PresentationFunction(f.getId(), f.getName(), roleIdsChecked.containsKey(f.getId())))
                .sorted(Comparator.comparing(PresentationFunction::getName)).toList());

        List<List<PresentationFunction>> groups = new ArrayList<>();
        for (int i = 0; i < list.size(); i += FOUR) {
            groups.add(list.subList(i, Math.min(i + FOUR, list.size())));
        }
        return groups;
    }

    private void setModelData(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("languagePrefList", LanguagePrefEnum.getWebList());
        model.addAttribute("personnelStatusList", PersonnelStatusEnum.getWebList());
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("isSuperAdmin", (AutorisationUtils.isRole("SUPER_ADMIN")));
        if (AutorisationUtils.getCurrentAuthorities().contains(SUPER_ADMIN)) {
            model.addAttribute("membersList", memberRepository.findAllProjectedBy());
        }
        model.addAttribute("roles", getAllFunctionsAndCheckedIfActive(user.getUserRoles()));
    }

    private UUID processUser(List<PresentationFunction> checked, User formUser) {
        User user;
        if (formUser.isNew()) {
            formUser.setPassword(AutorisationUtils.getCurrentMemberPassword());
            formUser.setMember(AutorisationUtils.getCurrentMember());
            user = userRepository.save(formUser);
        } else {
            user = formUser;
        }
        user.getUserRoles(); // lazy loading

        List<UUID> currentFunctionIdsDelete = new ArrayList<>(user.getUserRoles().stream().map(a -> a.getRole().getId()).toList());
        List<UUID> currentFunctionIdsAdd = new ArrayList<>(user.getUserRoles().stream().map(a -> a.getRole().getId()).toList());

        // initial so no data
        if (user.getUserRoles().isEmpty()) {
            checked.forEach(pf -> {
                Role role = roleRepository.findById(pf.getId()).get();
                UserRole userRole = new UserRole(user, role);
                user.getUserRoles().add(userRole);
                role.getUserRoles().add(userRole);
            });
        } else {
            // find the record to be deleted
            currentFunctionIdsDelete.removeAll(checked.stream().map(a -> a.getId()).toList());
            if (!currentFunctionIdsDelete.isEmpty()) {
                // delete records from list
                Set<UserRole> deleteRecord = user.getUserRoles().stream().filter(a -> currentFunctionIdsDelete.contains(a.getRole().getId())).collect(Collectors.toSet());
                roleRepository.deleteAllById(deleteRecord.stream().map(BaseBO::getId).collect(Collectors.toList()));
                user.setUserRoles(
                        user.getUserRoles().stream().filter(a -> !currentFunctionIdsDelete.contains(a.getRole().getId())).collect(Collectors.toSet())
                );
            }

            // find th record to be added
            List<PresentationFunction> pF = checked.stream().filter(a -> !currentFunctionIdsAdd.contains(a.getId())).toList();
            pF.forEach(pf -> {
                        Role role = roleRepository.findById(pf.getId()).get();
                        user.getUserRoles().add(new UserRole(user, role));
                    }
            );
        }

        userRoleRepository.saveAllAndFlush(user.getUserRoles());
        userRepository.save(user);
        return user.getId();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Form extends ToString {
        User user = new User();
        List<PresentationFunction> checkedFunctions = new ArrayList<>();
    }

}