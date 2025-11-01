package dwe.holding.customer.model.lookup;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity(name = "CUSTOMER_LOOKUPCOSTINGCATEGORY")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class LookupCostingCategory extends MemberBaseBO {
    @Column(name = "CATEGORY", nullable = false, length = 25)
    private String category;

    // TODO
//        @OneToMany(fetch = FetchType.LAZY, mappedBy = "lookupCostingCategory")
//        @Cascade({CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//        private Set<Costing> costings = new HashSet<>(0);
}