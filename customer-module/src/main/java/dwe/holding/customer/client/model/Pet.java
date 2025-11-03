package dwe.holding.customer.client.model;

import dwe.holding.customer.client.model.converter.SexTypeConverter;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.generic.admin.model.base.MemberBaseBO;
import dwe.holding.generic.shared.model.converter.YesNoEnumConverter;
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

// , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Entity(name = "CUSTOMER_PET")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Pet extends MemberBaseBO {
    @NotEmpty
    private String name;
    private LocalDate birthday;

    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deceased;
    private LocalDate deceasedDate;

    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum allergies;
    private String allergiesDescription;
    @Lob
    private String comments;

    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum gpwarning;
    private String gpwarningDescription;

    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum insured;
    private String insuredBy;

    private LocalDate chipDate;
    private String rabiesId;
    private String chipTattooId;
    private String briefDescription;

    private String species;
    private String breed;
    private String breedOther;
    @Column(columnDefinition = "varchar(1)")
    @Convert(converter = SexTypeConverter.class)
    private SexTypeEnum sex;
    private String idealWeight;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    private Set<Reminder> reminders = new HashSet<>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    private Set<Notes> notepads = new HashSet<>(0);

    @Transient
    public String getNameWithDeceased() {
        return (deceased.name().equals(YesNoEnum.No.name())) ? getName() : getName() + " &dagger;";
    }
}