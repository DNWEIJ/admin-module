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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_CUSTOMER")
// , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
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
    private BigDecimal balance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Builder.Default
    private Set<Pet> pets = new HashSet<Pet>(0);


    public String getAddress2() {
        return address2 == null ? "" : address2;
    }

    public static String getCustomerName(String lastName, String surName, String firstName, String middleInitial) {
        String CustomerName = lastName;

        if (surName != null && !surName.isEmpty()) {
            CustomerName = surName + " " + CustomerName;
        }
        if ((firstName != null) && (!firstName.isEmpty())) {
            CustomerName = CustomerName + ", " + firstName;
        }
        if ((middleInitial != null) && (!middleInitial.isEmpty())) {
            CustomerName = CustomerName + " " + middleInitial + (middleInitial.endsWith(".") ? "" : ".");
        }
        return CustomerName;
    }

    public String getSalutation() {
        String salutation = new String();

        if ((this.getTitle() != null) && (!this.getTitle().isEmpty())) {
            salutation += this.getTitle();
        }
        if ((this.getFirstName() != null) && (!this.getFirstName().isEmpty())) {
            salutation += (salutation.isEmpty() ? "" : " ") + this.getFirstName();
        }
        if ((this.getMiddleInitial() != null) && (!this.getMiddleInitial().isEmpty())) {
            salutation += (salutation.isEmpty() ? "" : " ") + this.getMiddleInitial();
        }
        if ((this.getLastName() != null) && (!this.getLastName().isEmpty())) {
            salutation += (salutation.isEmpty() ? "" : " ") + this.getLastName();
        }
        return salutation;
    }

    public String getCustomerName() {
        return getCustomerName(this.lastName, this.surName, this.firstName, this.middleInitial);
    }

    @Transient
    public String getCustomerNameWithId() {
        return getCustomerName() + " (" + getId() + ")";
    }

    public String getPhoneList() {
        StringBuffer phoneList = new StringBuffer();
        if (mobilePhone != null && !mobilePhone.isBlank()) {
            phoneList.append(mobilePhone);
        }
        if (homePhone != null && !homePhone.isBlank()) {
            if (phoneList.isEmpty()) {
                phoneList.append(homePhone);
            } else {
                phoneList.append(", ").append(homePhone);
            }
        }
        if (workPhone != null && !workPhone.isBlank()) {
            if (phoneList.isEmpty()) {
                phoneList.append(workPhone);
            } else {
                phoneList.append(", ").append(workPhone);
            }

        }
        return phoneList.toString();
    }

    public static String formattedHtmlAddress(String address2, String zipCode, String city) {
        return address2 + "<br/>" + zipCode + "&nbsp;&nbsp;" + city + "<br/>";

    }

    public String formattedHtmlAddress() {
        return formattedHtmlAddress(this.address2, this.zipCode, this.city);
    }

    public void setAddressIntoAddressLines() {
        this.address2 = this.street.trim() + " " + this.streetNumber;
        this.address3 = this.zipCode + " " + this.city.trim();
    }

    // constructor used in EnntityListDsls.java
    public Customer(
            Long id,
            String firstName,
            String surName,
            String lastName,
            String middleInitial,
            String email,
            YesNoEnum newsletter,
            String homePhone,
            String workPhone,
            String mobilePhone,
            String address2,
            String city,
            String zipCode,
            CustomerStatusEnum status,
            BigDecimal balance,
            Set<Pet> pets
    ) {
        this.setId(id);
        this.firstName = firstName;
        this.surName = surName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.email = email;
        this.newsletter = newsletter;
        this.homePhone = homePhone;
        this.workPhone = workPhone;
        this.mobilePhone = mobilePhone;
        this.address2 = address2;
        this.city = city;
        this.zipCode = zipCode;
        this.status = status;
        this.balance = balance;
        this.pets = pets;
    }
}