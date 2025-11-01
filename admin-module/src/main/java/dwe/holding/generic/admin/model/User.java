package dwe.holding.generic.admin.model;


import dwe.holding.generic.admin.model.base.BaseBO;
import dwe.holding.generic.admin.model.converter.LanguagePrefEnumConverter;
import dwe.holding.generic.admin.model.converter.PersonnelStatusEnumConverter;
import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;

import dwe.holding.generic.shared.model.converter.YesNoEnumConverter;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.*;

@Table(name = "ADMIN_USER", uniqueConstraints =
        {
                @UniqueConstraint(name = "uk_user_accountMemberId", columnNames = {"ACCOUNT", "MEMBER_ID"})
        })
@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class User extends BaseBO {
    @NotEmpty
    @Column(nullable = false)
    private String account;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = PersonnelStatusEnumConverter.class)
    private PersonnelStatusEnum personnelStatus;

    @Column(columnDefinition = "varchar(2)", nullable = false)
    @Convert(converter = LanguagePrefEnumConverter.class)
    private LanguagePrefEnum language;

    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum loginEnabled;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)

    @Builder.Default
    private boolean changePassword = false;
    private LocalDate lastVisitDate;
    private Long numberOfVisits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @Builder.Default
    private Member member = new Member();

    private   Long memberLocalId;

    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>(0);
    @Transient
    private List<String> roles = new ArrayList<>(0);
    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "userId")
    private List<IPSecurity> ipNumbers = new ArrayList<>(0);

    public User() {
        super();
    }
}