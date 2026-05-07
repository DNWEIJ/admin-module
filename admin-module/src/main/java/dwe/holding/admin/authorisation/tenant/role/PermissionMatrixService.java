package dwe.holding.admin.authorisation.tenant.role;

import dwe.holding.admin.authorisation.notenant.function.FunctionRepository;
import dwe.holding.admin.authorisation.notenant.function_role.InternalFunctionRoleRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionMatrixService {

    private final RoleRepository roleRepository;
    private final FunctionRepository functionRepository;
    private final InternalFunctionRoleRepository functionRoleRepository;

    public record IdName(Long id, String name) {
    }

    public record IdTypeName(Long id, String httpType, String name) {
    }

    public record IdAndId(Long functionId, Long roleId) {
    }

    public record IdAndIdAndValue(Long functionId, Long roleId, boolean add) {
    }

    // For maintenance, you need to login as the correct member since the roles are stored per member...
    public List<PermissionMatrixRow> buildMatrix() {
        List<IdName> roles = roleRepository.findAll().stream().sorted(Comparator.comparing(Role::getName)).map(r -> new IdName(r.getId(), r.getName())).toList();
        List<IdTypeName> functions = functionRepository.findAll().stream().sorted(Comparator.comparing(Function::getName)).map(f -> new IdTypeName(f.getId(), (f.getName().split("_")[0]), (f.getName().split("_")[1]))).toList();
        List<IdAndId> mappings = functionRoleRepository.findAll().stream().map(fr -> new IdAndId(fr.getFunctionId(), fr.getRoleId())).toList();

        // 2. Fast lookup for existing connections
        Set<String> connectedPairs = mappings.stream().map(m -> m.functionId() + ":" + m.roleId()).collect(Collectors.toSet());

        // 3. Build matrix
        List<PermissionMatrixRow> matrixRows = new ArrayList<>();

        for (IdTypeName function : functions) {
            PermissionMatrixRow row = new PermissionMatrixRow(function.id(), function.httpType(), function.name(), null);

            Map<Long, Boolean> rolePermissions = new LinkedHashMap<>();

            for (IdName role : roles) {
                boolean connected = connectedPairs.contains(
                        function.id() + ":" + role.id()
                );
                rolePermissions.put(role.id(), connected);
            }
            row.setRolePermissions(rolePermissions);
            matrixRows.add(row);
        }

        return matrixRows;
    }

    @Transactional
    void changeTheRelations(Map<String, RoleController.PermissionDto> uiLookupMap) {

        List<FunctionRole> dbRecs = functionRoleRepository.findAll();

        // index existing by (functionId, roleId)
        Map<String, FunctionRole> dbLookupMap = dbRecs.stream()
                .collect(Collectors.toMap(fr -> fr.getFunctionId() + "_" + fr.getRoleId(), fr -> fr, (a, b) -> a
                ));

        // CREATE: present in UI, not in DB
        List<FunctionRole> toCreate = uiLookupMap.entrySet().stream()
                .filter(e -> !dbLookupMap.containsKey(e.getKey()))
                .map(e -> {
                    FunctionRole a = FunctionRole.builder().functionId(e.getValue().functionId()).roleId(e.getValue().roleId()).build();
                    return a;
                }).toList();

        // DELETE: present in DB, not in UI
        List<FunctionRole> toDelete = dbLookupMap.entrySet().stream()
                .filter(e -> !uiLookupMap.containsKey(e.getKey())).map(Map.Entry::getValue).toList();

        System.out.println("nr of create: " + toCreate.size() + " nr of delete: " + toDelete.size());
        functionRoleRepository.deleteAllInBatch(toDelete);
        functionRoleRepository.saveAll(toCreate);
    }
}