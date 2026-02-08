package dwe.holding.customer.client.model.lookup;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_LOOKUP_SPECIES")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupSpecies extends MemberBaseBO {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "species")
    @Builder.Default
    private Set<LookupBreeds> breeds = new HashSet<>(0);
    @NotEmpty
    @Column(nullable = false)
    // todo plural -> single
    private String species;
}