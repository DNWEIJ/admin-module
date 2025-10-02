package dwe.holding.generic.app.suppliesandinventory.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SUPPLY_DISTRIBUTOR", uniqueConstraints = @UniqueConstraint(columnNames = {"DISTRIBUTOR_NAME"}))
@Entity
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
public class Distributor extends TenantBaseBO {
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
    private String comments;

    public Distributor() {

    }
}