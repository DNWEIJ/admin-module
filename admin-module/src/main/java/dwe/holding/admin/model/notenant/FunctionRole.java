package dwe.holding.admin.model.notenant;


import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Connect functions to a role.
 */
@Table(name = "ADMIN_FUNCTION_ROLE", uniqueConstraints = @UniqueConstraint(name = "uk_functionRole_funcIdRoelId", columnNames = {"FUNCTION_ID", "ROLE_ID"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FunctionRole extends TenantBaseBO {
    @Column(nullable = false)
    private Long functionId;

    @Column(nullable = false)
    private Long roleId;
}