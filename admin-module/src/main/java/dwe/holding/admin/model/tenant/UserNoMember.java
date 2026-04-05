package dwe.holding.admin.model.tenant;


import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.converter.LanguagePrefEnumConverter;
import dwe.holding.admin.model.converter.PersonnelStatusEnumConverter;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "ADMIN_USER")
@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
// This is the readonly user for validating the account before being logged in; no memberId available in securty context, so no member available on this domain object
// ALL FIELDS ARE DUPLICATE, MAINTAIN THE USER AS WELL
public class UserNoMember extends BaseBO {
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

    @Column(columnDefinition = "varchar(1)", nullable = false)
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

    @Transient
    private MemberNoMember member;

    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>(0);
    @Builder.Default
    @Transient
    private List<String> roles = new ArrayList<>(0);
    @Builder.Default
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "userId")
    private List<IPSecurity> ipNumbers = new ArrayList<>(0);

    // specific for no member
    @Column(nullable = false)
    private Long memberId;

    @Transient
    boolean newEncryptionPassword;
}