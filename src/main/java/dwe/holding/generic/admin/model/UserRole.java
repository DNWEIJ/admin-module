package dwe.holding.generic.admin.model;


import dwe.holding.generic.admin.model.base.BaseBO;
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
public class UserRole extends BaseBO {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Role role;
}