package dwe.holding.customer.client.model.lookup;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "CUSTOMER_LOOKUP_BREEDS")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupBreeds extends MemberBaseBO {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lookupspecies_id", nullable = false)
    private LookupSpecies species;
    @Column(nullable = false)
    @NotEmpty
    private String speciesName;
    @Column(nullable = false)
    @NotEmpty
    private String breed;
}