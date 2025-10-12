package dwe.holding.generic.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.model.lookup.LookupLocation;
import jakarta.persistence.*;
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
    public  class Location extends TenantBaseBO {

        @Column(nullable = false)
        private Long diagnoseId;

        @Column(nullable = false)
        private Long lookupLocationId;


        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "LOOKUP_LOCATION_ID", insertable = false, updatable = false)
        private LookupLocation lookupLocation;
    }