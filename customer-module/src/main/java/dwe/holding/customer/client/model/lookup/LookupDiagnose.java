package dwe.holding.customer.client.model.lookup;

import dwe.holding.customer.client.model.Diagnose;
import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "CUSTOMER_LOOKUP_DIAGNOSE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupDiagnose extends MemberBaseBO implements Serializable {
    @Column(nullable = false)
    @NotEmpty
    private String nomenclature;
    private Long venomId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "detail")
    private Set<Diagnose> diagnoses = new HashSet<>(0);
}