package dwe.holding.customer.model.lookup;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "CUSTOMER_LOOKUPSPECIES")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupSpecies extends TenantBaseBO {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "species")
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<LookupBreeds> breeds = new HashSet<>(0);
    private String species;
}