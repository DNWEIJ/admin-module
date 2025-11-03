package dwe.holding.customer.client.model.lookup;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "CUSTOMER_LOOKUP_SPECIES")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupSpecies extends MemberBaseBO {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "species")
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<LookupBreeds> breeds = new HashSet<>(0);
    @NotEmpty
    @Column(nullable = false)
    private String species;
}