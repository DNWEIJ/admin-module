package dwe.holding.admin.authorisation.tenant.role;

import dwe.holding.admin.model.tenant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')") // TODO fvalidate
@RequestMapping("/admin")
@AllArgsConstructor
public class RoleController {
    private final RoleRepository roleRepository;
    private final PermissionMatrixService permissionMatrixService;

    @PostMapping("/role")
    String save(Role role) {
        if (role.getId() == null) {
            roleRepository.save(Role.builder().name(role.getName()).build());
        } else {
            Role roleRetrieved = roleRepository.findById(role.getId()).orElseThrow();
            roleRetrieved.setName(role.getName());
            roleRepository.save(roleRetrieved);
        }
        return "redirect:/admin/roles";
    }

    @GetMapping("/role")
    String newScreen(Model model) {
        model
                .addAttribute("action", "Create")
                .addAttribute("role", new Role());
        return "admin-module/role/roleaction";

    }

    @GetMapping("/role/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model
                .addAttribute("action", "Edit")
                .addAttribute("role", roleRepository.findById(id).orElseThrow())
        ;
        return "admin-module/role/roleaction";
    }

    @GetMapping("/roles")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("roles", roleRepository.findAll());
        return "admin-module/role/list";
    }

    @GetMapping("/roles/mapping")
    public String getMappigFunctionRole(Model model) {
        model.addAttribute("matrix", permissionMatrixService.buildMatrix());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin-module/role/functionrolemappingaction";
    }

    public record PermissionDto(Long functionId, Long roleId) {
    }

    @PostMapping("/roles/savematrix")
    public String saveMatrix(@RequestParam Map<String, String> params) {

        Map<String, PermissionDto> permissions = new HashMap<>();
        // Extract permissions map
        params.forEach((key, value) -> {
            // permissions[funcId][roleId]
            if (key.startsWith("permissions")) {
                Long funcId = Long.valueOf(key.split("\\[")[1].replace("]", ""));
                Long roleId = Long.valueOf(key.split("\\[")[2].replace("]", ""));
                permissions.put(funcId + "_" + roleId, new PermissionDto(funcId, roleId)
                );
            }
        });
        permissionMatrixService.changeTheRelations(permissions);
        return "redirect:/admin/roles/mapping";
    }
}