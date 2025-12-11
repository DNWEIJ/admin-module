package dwe.holding.customer.client.model.lookup;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.Diagnose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_LOOKUP_DIAGNOSE")
@Entity
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

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "detail")
    private Set<Diagnose> diagnoses = new HashSet<>(0);
}