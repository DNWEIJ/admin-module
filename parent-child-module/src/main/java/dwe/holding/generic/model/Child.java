package dwe.holding.generic.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.model.type.SexTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Table(name = "PC_CHILD") // , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Child extends TenantBaseBO {

    private String name;
    private Date birthday;
    private YesNoEnum deceased;
    private Date deceasedDate;
    private YesNoEnum allergies;
    private String allergiesDescription;
    private String comments;

    private YesNoEnum gpwarning;
    private String dangerousDescription;

    private YesNoEnum insured;
    private String insuredBy;
    private Date chipDate;
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
    @JoinColumn(name = "parent_id")
    private Parent parent;


    //    /**
//     *  Diagnose belonging to this Patients (Pets).
//     */
//    private Set<Diagnose> diagnoses = new HashSet<Diagnose>(0);
//    /**
//     *  Visits belonging to this Patients (Pets).
//     */
//    private Set<Visit> visits = new HashSet<Visit>(0);
//    /**
//     *  Estimatespecifics belonging to this Patients (Pets).
//     */
//    private Set<Estimatespecific> estimatespecifics = new HashSet<Estimatespecific>(0);
//    /**
//     *  Reminds belong to this Patients(Pet).
//     */
//    private Set<Reminder> reminders = new HashSet<Reminder>(0);
//    /**
//     * Note pads belong to this Patient(Pet).
//     */
//    private Set<Notepad> notepads = new HashSet<Notepad>(0);
}