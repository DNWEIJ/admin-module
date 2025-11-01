package dwe.holding.customer.model;


import dwe.holding.customer.model.lookup.LookupDiagnose;
import dwe.holding.generic.admin.model.base.MemberBaseBO;
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
public class Diagnose extends MemberBaseBO {

    @Column(nullable = false)
    @NotNull
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "diagnose")
    private Set<Location> locations = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lookupdiagnosis_id", nullable = false)
    private LookupDiagnose detail;

}