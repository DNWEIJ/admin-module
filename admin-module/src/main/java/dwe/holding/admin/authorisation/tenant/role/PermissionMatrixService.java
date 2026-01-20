package dwe.holding.admin.authorisation.tenant.role;

import dwe.holding.admin.authorisation.notenant.function_role.FunctionRoleRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionMatrixService {

    private final RoleRepository roleRepository;
    private final FunctionRepository functionRepository;
    private final FunctionRoleRepository functionRoleRepository;

    public PermissionMatrixService(RoleRepository roleRepository,
                                   FunctionRepository functionRepository,
                                   FunctionRoleRepository functionRoleRepository) {
        this.roleRepository = roleRepository;
        this.functionRepository = functionRepository;
        this.functionRoleRepository = functionRoleRepository;
    }

    public List<PermissionMatrixRow> buildMatrix() {

        // 1. Load everything
        List<Role> roles = roleRepository.findAll();
        List<Function> functions = functionRepository.findAll();
        List<FunctionRole> mappings = functionRoleRepository.findAll();

        // Optional but recommended: sort for stable UI
        roles.sort(Comparator.comparing(Role::getName));
        functions.sort(Comparator.comparing(Function::getName));

        // 2. Fast lookup for existing connections
        Set<String> connectedPairs = mappings.stream()
                .map(m -> m.getId() + ":" + m.getRoleId())
                .collect(Collectors.toSet());

        // 3. Build matrix
        List<PermissionMatrixRow> matrixRows = new ArrayList<>();

        for (Function function : functions) {
            PermissionMatrixRow row = new PermissionMatrixRow(function.getId(), function.getName(), null);

            Map<Long, Boolean> rolePermissions = new LinkedHashMap<>();

            for (Role role : roles) {
                boolean connected = connectedPairs.contains(
                        function.getId() + ":" + role.getId()
                );
                rolePermissions.put(role.getId(), connected);
            }

            row.setRolePermissions(rolePermissions);
            matrixRows.add(row);
        }

        return matrixRows;
    }
}