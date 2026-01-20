package dwe.holding.admin.authorisation.tenant.role;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class PermissionMatrixRow {
    private Long functionId;
    private String functionName;
    private Map<Long, Boolean> rolePermissions  = new LinkedHashMap<>();
    // key = roleId, value = connected
}