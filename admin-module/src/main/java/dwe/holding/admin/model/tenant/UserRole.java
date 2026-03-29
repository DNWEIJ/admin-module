package dwe.holding.admin.model.tenant;


import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Connect role and user.
 */

@Table(name = "ADMIN_USER_ROLE"
        , uniqueConstraints = @UniqueConstraint(name = "uk_userRole_userIdRoleId", columnNames = {"USER_ID", "ROLE_ID"})
)
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends MemberBaseBO {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Role role;
}