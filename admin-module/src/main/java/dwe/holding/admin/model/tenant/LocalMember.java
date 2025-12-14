package dwe.holding.admin.model.tenant;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Table(name = "ADMIN_LOCALMEMBER", uniqueConstraints = @UniqueConstraint(name = "uk_memberLocal_name", columnNames = "local_member_name"))
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LocalMember extends MemberBaseBO {
    @Column(nullable = false)
    private String localMemberName;
    private String phone1;
    private String phone2;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zipCode;
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "localMember")
    private List<LocalMemberTax> memberLocalTaxs = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private MetaLocalMemberPreferences metaLocalMemberPreferences;
}