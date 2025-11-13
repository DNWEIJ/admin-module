package dwe.holding.customer.client.model;

import dwe.holding.customer.client.model.converter.SexTypeConverter;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
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

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deceased;
    private LocalDate deceasedDate;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum allergies;
    private String allergiesDescription;
    @Lob
    private String comments;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum gpwarning;
    private String gpwarningDescription;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum insured;
    private String insuredBy;

    private String passportNumber;
    private LocalDate chipDate;
    private String chipTattooId;
    private String briefDescription;

    private String species;
    private String breed;
    private String breedOther;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = SexTypeConverter.class)
    private SexTypeEnum sex;
    private String idealWeight;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    @Builder.Default
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
    @Builder.Default
    private Set<Reminder> reminders = new HashSet<>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    @Builder.Default
    private Set<Notes> notepads = new HashSet<>(0);

    @Transient
    public String getNameWithDeceased() {
        return (deceased.name().equals(YesNoEnum.No.name())) ? getName() : getName() + " &dagger;";
    }

    @Transient
    public String getAge() {
        LocalDate today = LocalDate.now();
        return getBirthday() == null ? "" : getBirthday().until(today).getYears() + " years " + getBirthday().until(today).getMonths() + " months";
    }

}