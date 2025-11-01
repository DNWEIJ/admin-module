package dwe.holding.customer.model.lookup;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "CUSTOMER_LOOKUPBREEDS")
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public  class LookupBreeds extends MemberBaseBO {
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "lookupspecies_id", nullable = false)
        private LookupSpecies species;
        private String speciesName;
        private String breed;
    }