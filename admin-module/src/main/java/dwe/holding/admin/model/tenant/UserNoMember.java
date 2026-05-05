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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "ADMIN_USER")
@Entity
@Getter
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
    @Setter
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

    @Setter
    private boolean changePassword = false;
    @Setter
    private LocalDate lastVisitDate;
    @Setter
    private Long numberOfVisits;

    @Transient
    @Setter
    private MemberNoMember member;


    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>(0);

    @Transient
    private List<String> roles = new ArrayList<>(0);

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "userId")
    private List<IPSecurity> ipNumbers = new ArrayList<>(0);

    // specific for no member
    @Column(nullable = false)
    private Long memberId;

    @Transient
    @Setter
    boolean newEncryptionPassword;
}