package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "CONSULT_DIAGNOSE")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Diagnose extends MemberBaseBO {

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @NotNull
    @Column(nullable = false)
    private Long petId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lookup_diagnose_id")
    private LookupDiagnose lookupDiagnose;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lookup_location_id")
    private LookupLocation lookupLocation;

}