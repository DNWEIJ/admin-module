package dwe.holding.customer.client.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.converter.CustomerStatusConverter;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_CUSTOMER") // , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer extends MemberBaseBO {

    @NotEmpty
    @Column(nullable = false)
    private String firstName;
    private String surName;
    @NotEmpty
    @Column(nullable = false)
    private String lastName;
    private String middleInitial;
    private String title;
    private String email;

    private String extraAddressInfo;
    private String address2;
    private String address3;

    private String oldAddressInfo;

    @Pattern(regexp = "^[1-9][0-9]{3}\\s?[A-Z]{2}$")
    private String zipCode;
    private String street;
    private String streetNumber;
    private String city;

    private String homePhone;
    private String workPhone;
    private String mobilePhone;

    private String emergencyContact;
    private String emergencyContactPhone;

    private String previousVeterinarian;
    private String previousVeterinarianPhone;

    @Lob
    private String comments;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = CustomerStatusConverter.class)
    private CustomerStatusEnum status;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum newsletter;

    private String ubn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Builder.Default
    private Set<Pet> pets = new HashSet<Pet>(0);

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
//    private Set<Payment> payments = new HashSet<Payment>(0);

    public String getCustomerName() {
        String CustomerName = getLastName();

        if(getSurName() != null && !getSurName().isEmpty()) {
            CustomerName = getSurName() + " " + CustomerName;
        }
        if((getFirstName() != null) && (!getFirstName().isEmpty())) {
            CustomerName = CustomerName + ", " + getFirstName();
        }
        if((getMiddleInitial() != null) && (!getMiddleInitial().isEmpty())) {
            CustomerName = CustomerName + " " + getMiddleInitial() + (getMiddleInitial().endsWith(".") ? "" : ".");

        }
        return CustomerName;
    }

    @Transient
    public String getCustomerNameWithId() {
        return getCustomerName() + " (" + getId() + ")";
    }
}