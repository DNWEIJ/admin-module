package dwe.holding.admin.authorisation.tenant.role;

import dwe.holding.admin.authorisation.notenant.function_role.FunctionRoleRepository;
import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.base.ToString;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static dwe.holding.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admin")
@AllArgsConstructor
public class RoleController {
    public static final int FOUR = 4;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final FunctionRepository functionRepository;
    private final PermissionMatrixService permissionMatrixService;

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
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
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
        model.addAttribute("functions", getAllFunctionsAndCheckedIfActive(role.getId(), model));
        model.addAttribute("roles", roleRepository.findAll()); // List<Role>
        model.addAttribute("matrix", permissionMatrixService.buildMatrix()); // List<PermissionMatrixRow>

    }

    @PostMapping("/roles/saveMatrix")
    public String saveMatrix(@RequestParam Map<String, String> params) {

        // Extract permissions map
        params.forEach((key, value) -> {
            // permissions[funcId][roleId]
            if (key.startsWith("permissions")) {
                String funcId = key.split("\\[")[1].replace("]", "");
                String roleId = key.split("\\[")[2].replace("]", "");

                // save mapping funcId ↔ roleId
            }
        });

        return " :/roles";
    }


    private List<List<PresentationElement>> getAllFunctionsAndCheckedIfActive(Long roleId, Model model) {
        List<Function> functions = functionRepository.findAll();
        List<FunctionRole> functionsForRole = functionRoleRepository.findByRoleId(roleId);
        Map<Long, String> functionIds = functions.stream().collect(Collectors.toMap(Function::getId, Function::getName));

        Map<Long, @NotEmpty String> functionIdsChecked = functionsForRole.stream().collect(
                Collectors.toMap(s -> s.getFunctionId(), s -> functionIds.get(s.getFunctionId()))
        );

        List<PresentationElement> list = new ArrayList<>(functions.stream()
                .map(f -> new PresentationElement(f.getId(), f.getName(), functionIdsChecked.containsKey(f.getId())))
                .sorted(Comparator.comparing(PresentationElement::getName)).toList());
        model.addAttribute("elements", list);
        List<List<PresentationElement>> groups = new ArrayList<>();
        for (int i = 0; i < list.size(); i += FOUR) {
            groups.add(list.subList(i, Math.min(i + FOUR, list.size())));
        }
        return groups;
    }

    private Long processRole(List<PresentationElement> checked, Role formRole) {
        Role role;

        if (formRole.isNew()) {
            role = roleRepository.save(formRole);
        } else {
            role = formRole;
        }
        List<FunctionRole> functionsForRole = functionRoleRepository.findByRoleId(role.getId());

        List<Long> currentFunctionIdsDelete = new ArrayList<>(functionsForRole.stream().map(a -> a.getFunctionId()).toList());
        List<Long> currentFunctionIdsAdd = new ArrayList<>(functionsForRole.stream().map(a -> a.getFunctionId()).toList());

        // initial so no data
        if (functionsForRole.isEmpty()) {
            for (PresentationElement pf : checked) {
                Function function = functionRepository.findById(pf.getLongId()).orElseThrow();
                FunctionRole functionRole = new FunctionRole(function.getId(), role.getId());
                functionsForRole.add(functionRole);
            }
            ;
        } else {
            // find the record to be deleted
            currentFunctionIdsDelete.removeAll(checked.stream().map(a -> a.getId()).toList());
            if (!currentFunctionIdsDelete.isEmpty()) {
                // delete records from list
                Set<FunctionRole> deleteRecord = functionsForRole.stream().filter(a -> currentFunctionIdsDelete.contains(a.getFunctionId())).collect(Collectors.toSet());
                functionRoleRepository.deleteAllById(deleteRecord.stream().map(BaseBO::getId).collect(Collectors.toList()));
                functionsForRole = functionsForRole.stream().filter(a -> !currentFunctionIdsDelete.contains(a.getFunctionId())).collect(Collectors.toList());
            }

            // find th record to be added
            List<PresentationElement> pF = checked.stream().filter(a -> !currentFunctionIdsAdd.contains(a.getId())).toList();
            pF.forEach(pf -> {
                        Function function = functionRepository.findById(pf.getLongId()).orElseThrow();
// TODO: Ensure this works correctly
                        //                        functionsForRole.add(new FunctionRole(function.getId(), role.getId()));
                    }
            );
        }

        functionRoleRepository.saveAllAndFlush(functionsForRole);
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