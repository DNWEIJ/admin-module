package dwe.holding.generic.admin.model;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@Table(name = "ADMIN_ROLE", uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "NAME")
)
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role extends MemberBaseBO {

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "role")
    @Builder.Default
    private Set<FunctionRole> functionRoles = new HashSet<FunctionRole>(1);

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "role")
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<UserRole>(1);
}