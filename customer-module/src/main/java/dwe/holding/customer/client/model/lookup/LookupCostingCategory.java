package dwe.holding.customer.client.model.lookup;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity(name = "CUSTOMER_LOOKUP_COSTING_CATEGORY")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class LookupCostingCategory extends MemberBaseBO {
    @Column(nullable = false)
    @NotEmpty
    private String category;

    // TODO
//        @OneToMany(fetch = FetchType.LAZY, mappedBy = "lookupCostingCategory")
//        @Cascade({CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//        private Set<Costing> costings = new HashSet<>(0);
}