package dwe.holding.generic.admin.model;


import dwe.holding.generic.admin.model.base.BaseBO;
import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "ADMIN_USER", uniqueConstraints = @UniqueConstraint(name = "uk_user_accountPassword", columnNames = {"ACCOUNT", "PASSWORD"}))
@Entity
@SuperBuilder
@Getter
@Setter
public class User extends BaseBO {
    @NotEmpty
    @Column(nullable = false)
    private String account;
    @NotEmpty
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)

    @Enumerated(EnumType.STRING)
    private PersonnelStatusEnum personnelStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguagePrefEnum language;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private YesNoEnum loginEnabled;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    @Builder.Default
    private boolean changePassword = true;
    private LocalDate lastVisitDate;
    private Long numberOfVisits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @Builder.Default
    private Member member = new Member();
    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<UserRole>(0);
    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "ipnumber")
    private Set<IPSecurity> ipNumbers = new HashSet<IPSecurity>(0);

    public User() {
        super();
    }
}