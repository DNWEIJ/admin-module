package dwe.holding.admin.model;

import dwe.holding.admin.model.base.BaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Table(name = "ADMIN_FUNCTION", uniqueConstraints = @UniqueConstraint(name = "uk_function_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Function extends BaseBO {

    @NotEmpty
    @Column(nullable = false)
    private String name;

// TODO:"Add app name to member, so we can have functions per app and only show them
//    @NotEmpty
//    @Column(nullable = false)
//    private String appName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "function")
    @Builder.Default
    private Set<FunctionRole> functionRoles = new HashSet<FunctionRole>(0);
}