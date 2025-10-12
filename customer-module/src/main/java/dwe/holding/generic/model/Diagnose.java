package dwe.holding.generic.model;


import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.model.lookup.LookupDiagnose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "CUSTOMER_DIAGNOSE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Diagnose extends TenantBaseBO{

    @Column(nullable = false)
    @NotNull
    private Long appointmentId;

    @Column(nullable = false)
    @NotNull
    private Long patientId;

    /**
     * Where the diagnose is located on the animal.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "unresolved")
        private Set<Location> locations = new HashSet<>(0);

    /**
     * Detail of the diagnose.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOOKUPDIAGNOSIS_ID", nullable = false)
    private LookupDiagnose detail;

}