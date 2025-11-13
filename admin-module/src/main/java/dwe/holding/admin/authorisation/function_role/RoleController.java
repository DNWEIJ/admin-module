package dwe.holding.admin.authorisation.function_role;

import dwe.holding.admin.model.Function;
import dwe.holding.admin.model.FunctionRole;
import dwe.holding.admin.model.Role;
import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.base.ToString;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static dwe.holding.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admin")
public class RoleController {
    public static final int FOUR = 4;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final FunctionRepository functionRepository;

    public RoleController(RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, FunctionRepository functionRepository) {
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
        this.functionRepository = functionRepository;
    }

    @PostMapping("/role")
    String save(@Valid Form form, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/role/action";
        }
          Long roleId = processRole(form.checkedFunctions, form.role);

        redirect.addFlashAttribute("message", "Role saved successfully!");

        return getRedirectFor(request, roleId, "redirect:/role");
    }

    @GetMapping("/role")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new Role());
        return "admin-module/role/action";

    }

    @GetMapping("/role/{id}")
    String showEditScreen(@PathVariable @NotNull   Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, roleRepository.findById(id).orElseThrow());
        return "admin-module/role/action";
    }

    @GetMapping("/role/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("roles", roleRepository.findAll());
        return "admin-module/role/list";
    }

    private void setModelData(Model model, Role role) {
        model.addAttribute("role", role);
        model.addAttribute("functions", getAllFunctionsAndCheckedIfActive(role.getFunctionRoles()));
    }

    private List<List<PresentationElement>> getAllFunctionsAndCheckedIfActive(Set<FunctionRole> functionRoleSet) {
        List<Function> functions = functionRepository.findAll();
        Map<  Long, @NotEmpty String> functionIdsChecked = functionRoleSet.stream().collect(
                Collectors.toMap(s -> s.getFunction().getId(), s -> s.getFunction().getName())
        );

        List<PresentationElement> list = new ArrayList<>(functions.stream()
                .map(f -> new PresentationElement(f.getId(), f.getName(), functionIdsChecked.containsKey(f.getId())))
                .sorted(Comparator.comparing(PresentationElement::getName)).toList());
        List<List<PresentationElement>> groups = new ArrayList<>();
        for (int i = 0; i < list.size(); i += FOUR) {
            groups.add(list.subList(i, Math.min(i + FOUR, list.size())));
        }
        return groups;
    }

    private   Long processRole(List<PresentationElement> checked, Role formRole) {
        Role role;

        if (formRole.isNew()) {
            role = roleRepository.save(formRole);
        } else {
            role = formRole;
        }
        role.getFunctionRoles(); // lazy loading

        List<  Long> currentFunctionIdsDelete = new ArrayList<>(role.getFunctionRoles().stream().map(a -> a.getFunction().getId()).toList());
        List<  Long> currentFunctionIdsAdd = new ArrayList<>(role.getFunctionRoles().stream().map(a -> a.getFunction().getId()).toList());

        // initial so no data
        if (role.getFunctionRoles().isEmpty()) {
            checked.forEach(pf -> {
                Function function = functionRepository.findById(pf.id).orElseThrow();
                FunctionRole functionRole = new FunctionRole(function, role);
                function.getFunctionRoles().add(functionRole);
                role.getFunctionRoles().add(functionRole);
            });
        } else {
            // find the record to be deleted
            currentFunctionIdsDelete.removeAll(checked.stream().map(a -> a.getId()).toList());
            if (!currentFunctionIdsDelete.isEmpty()) {
                // delete records from list
                Set<FunctionRole> deleteRecord = role.getFunctionRoles().stream().filter(a -> currentFunctionIdsDelete.contains(a.getFunction().getId())).collect(Collectors.toSet());
                functionRoleRepository.deleteAllById(deleteRecord.stream().map(BaseBO::getId).collect(Collectors.toList()));
                role.setFunctionRoles(
                        role.getFunctionRoles().stream().filter(a -> !currentFunctionIdsDelete.contains(a.getFunction().getId())).collect(Collectors.toSet())
                );
            }

            // find th record to be added
            List<PresentationElement> pF = checked.stream().filter(a -> !currentFunctionIdsAdd.contains(a.getId())).toList();
            pF.forEach(pf -> {
                        Function function = functionRepository.findById(pf.id).orElseThrow();
                        role.getFunctionRoles().add(new FunctionRole(function, role));
                    }
            );
        }

        functionRoleRepository.saveAllAndFlush(role.getFunctionRoles());
        roleRepository.save(role);
        return role.getId();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Form extends ToString {
        Role role = new Role();
        List<PresentationElement> checkedFunctions = new ArrayList<>();
    }
}