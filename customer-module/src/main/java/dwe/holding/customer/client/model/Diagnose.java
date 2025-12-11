package dwe.holding.customer.client.model;


import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.lookup.LookupDiagnose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_DIAGNOSE")
@Entity
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
    @Builder.Default
    private Set<Location> locations = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lookupdiagnosis_id", nullable = false)
    private LookupDiagnose detail;

}