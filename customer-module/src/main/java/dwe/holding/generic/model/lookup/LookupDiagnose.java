package dwe.holding.generic.model.lookup;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.model.Diagnose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "CUSTOMER_LOOKUPDIAGNOSE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupDiagnose extends TenantBaseBO implements Serializable {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "detail")
    private Set<Diagnose> diagnoses = new HashSet<>(0);
    private String nomenclature;
    private Long venomId;
}