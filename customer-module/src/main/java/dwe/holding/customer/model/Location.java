package dwe.holding.customer.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "CUSTOMER_LOCATION")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Location extends TenantBaseBO {

    @ManyToOne
    @JoinColumn(name = "diagnose_id")
    private Diagnose diagnose;

    @Column(nullable = false)
    private Long lookupLocationId;
}