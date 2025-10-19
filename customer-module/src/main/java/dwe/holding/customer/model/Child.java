package dwe.holding.customer.model;

import dwe.holding.customer.model.type.SexTypeEnum;
import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CUSTOMER_CHILD") // , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Child extends TenantBaseBO {
    @NotEmpty
    private String name;
    private LocalDate birthday;

    private YesNoEnum deceased;
    private LocalDate deceasedDate;

    private YesNoEnum allergies;
    private String allergiesDescription;

    private String comments;

    private YesNoEnum gpwarning;
    private String dangerousDescription;

    private YesNoEnum insured;
    private String insuredBy;

    private LocalDate chipDate;
    private String rabiesId;
    private String chipTattooId;
    private String briefDescription;

    private String species;
    private String breed;
    private String breedOther;
    @Enumerated(EnumType.STRING)
    private SexTypeEnum sex;
    private String idealWeight;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "child")
    private Set<Diagnose> diagnoses = new HashSet<>(0);
    // TODO
//    /**
//     *  Visits belonging to this Patients (Pets).
//     */
//    private Set<Visit> visits = new HashSet<Visit>(0);
//    /**
//     *  Estimatespecifics belonging to this Patients (Pets).
//     */
//    private Set<Estimatespecific> estimatespecifics = new HashSet<Estimatespecific>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "child")
    private Set<Reminder> reminders = new HashSet<>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "child")
    private Set<Notes> notepads = new HashSet<>(0);
}