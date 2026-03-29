package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLY_DISTRIBUTOR", uniqueConstraints = @UniqueConstraint(columnNames = {"DISTRIBUTOR_NAME"}))
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Distributor extends MemberBaseBO {
    @NotEmpty
    @Column(nullable = false)
    private String distributorName;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zipCode;
    private String phone1;
    private String phone2;
    private String fax;
    private String email;
    private String url;
    @Lob
    private String comments;

    public String getAddress() {
        String address = address1;

        if (address2 != null && !address2.isEmpty()) {
            address = address + " " + address2;
        }
        if (address3 != null && !address3.isEmpty()) {
            address = address + " " + address3;
        }
        return address;
    }

    public String getPhone() {
        String phone = phone1;

        if (phone2 != null && !phone2.isEmpty()) {
            phone = phone + " " + phone2;
        }
        return phone;
    }
}