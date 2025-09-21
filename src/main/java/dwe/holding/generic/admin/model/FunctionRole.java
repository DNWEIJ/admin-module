package dwe.holding.generic.admin.model;


import dwe.holding.generic.admin.model.base.BaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Connect funtions to a role.
 */
@Table(name = "ADMIN_FUNCTION_ROLE", uniqueConstraints = @UniqueConstraint(name = "uk_functionRole_funcIdRoelId", columnNames = {"FUNCTION_ID", "ROLE_ID"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FunctionRole extends BaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Function function;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Role role;

}