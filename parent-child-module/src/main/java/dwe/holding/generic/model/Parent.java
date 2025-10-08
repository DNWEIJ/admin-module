package dwe.holding.generic.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.model.type.CustomerStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.HashSet;
import java.util.Set;

@Table(name = "PC_PARENT") // , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Parent extends TenantBaseBO {

    @NotEmpty
    @Column(nullable = false)
    private String lastName;
    @NotEmpty
    @Column(nullable = false)
    private String firstName;
    private String surname;
    private String middleInitial;

    private String title;
    private String email;
    private String address1;
    private String address2;
    private String address3;
    @NotEmpty
    @Column(nullable = false)
    private String city;
    private String state;
    @NotEmpty
    @Column(nullable = false)
    private String zipCode;
    private String homePhone;
    private String workPhone;
    private String mobilePhone;
    private String emergencyContact;
    private String emergencyContactPhone;

    private String previousVeterinarian;
    private String previousVeterinarianPhone;
    private String comments;
    @Enumerated(EnumType.STRING)
    private CustomerStatusEnum status;
    @Enumerated(EnumType.STRING)
    private YesNoEnum newsletter;
    private String ubn;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
    @Builder.Default
    private Set<Child> children = new HashSet<Child>(0);

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
//    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
//    private Set<Payment> payments = new HashSet<Payment>(0);

}