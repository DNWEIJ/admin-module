package dwe.holding.generic.admin.authorisation.user;

import dwe.holding.generic.admin.authorisation.function_role.RoleRepository;
import dwe.holding.generic.admin.authorisation.function_role.UserRoleRepository;
import dwe.holding.generic.admin.authorisation.member.MemberRepository;
import dwe.holding.generic.admin.transactional.TranactionalUser;
import dwe.holding.generic.shared.model.frontend.PresentationElement;
import dwe.holding.generic.admin.model.Role;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserRole;
import dwe.holding.generic.admin.model.base.BaseBO;
import dwe.holding.generic.admin.model.base.ToString;
import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;


@Controller
@Validated
@RequestMapping("/admin")
public class UserController {
    public static final int FOUR = 4;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final TranactionalUser tranactionalUser;


    public UserController(UserRoleRepository userRoleRepository, RoleRepository roleRepository, MemberRepository memberRepository, TranactionalUser tranactionalUser) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
        this.tranactionalUser = tranactionalUser;
    }

    @PostMapping("/user")
    String save(@Valid Form form, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/user/action";
        }
          Long userId = processUser(form.checkedFunctions, form.user);
        redirect.addFlashAttribute("message", "Role saved successfully!");
        return getRedirectFor(request, userId, "redirect:/user");
    }

    @GetMapping("/user")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new User());
        return "admin-module/user/action";

    }

    @GetMapping("/user/{id}")
    String showEditScreen(@PathVariable @NotNull   Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, tranactionalUser.getByIdLazy_RolesAndIpNumbers(id));
        return "admin-module/user/action";
    }

    @GetMapping("/user/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("users", tranactionalUser.findAll());
        return "admin-module/user/list";
    }

    private List<List<PresentationElement>> getAllFunctionsAndCheckedIfActive(Set<UserRole> userRoles) {
        if (userRoles == null ) return new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        Map<  Long, String> roleIdsChecked = userRoles.stream().collect(
                Collectors.toMap(s -> s.getRole().getId(), s -> s.getRole().getName())
        );

        List<PresentationElement> list = new ArrayList<>(roles.stream()
                .map(f -> new PresentationElement(f.getId(), f.getName(), roleIdsChecked.containsKey(f.getId())))
                .sorted(Comparator.comparing(PresentationElement::getName)).toList());

        List<List<PresentationElement>> groups = new ArrayList<>();
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
        model.addAttribute("membersList", memberRepository.findAllProjectedBy()
                .stream()
                .map(member -> new PresentationElement(member.id(), member.name(), false))
                .toList());
        model.addAttribute("roles", getAllFunctionsAndCheckedIfActive(user.getUserRoles()));

        if (AutorisationUtils.hasRole("SUPER_ADMIN")) {
            model.addAttribute("isSuperAdmin", true);
        } else {
            model.addAttribute("isSuperAdmin", false);
        }
    }

    private   Long processUser(List<PresentationElement> checked, User formUser) {
        User user;
        if (formUser.isNew()) {
            formUser.setPassword(AutorisationUtils.getCurrentMemberPassword());
            formUser.setMember(AutorisationUtils.getCurrentMember());
            user = tranactionalUser.save(formUser);
        } else {
            user = formUser;
        }
        user.getUserRoles(); // lazy loading

        List<  Long> currentFunctionIdsDelete = new ArrayList<>(user.getUserRoles().stream().map(a -> a.getRole().getId()).toList());
        List<  Long> currentFunctionIdsAdd = new ArrayList<>(user.getUserRoles().stream().map(a -> a.getRole().getId()).toList());

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
            List<PresentationElement> pF = checked.stream().filter(a -> !currentFunctionIdsAdd.contains(a.getId())).toList();
            pF.forEach(pf -> {
                        Role role = roleRepository.findById(pf.getId()).get();
                        user.getUserRoles().add(new UserRole(user, role));
                    }
            );
        }

        userRoleRepository.saveAllAndFlush(user.getUserRoles());
        tranactionalUser.save(user);
        return user.getId();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Form extends ToString {
        User user = new User();
        List<PresentationElement> checkedFunctions = new ArrayList<>();
    }

}