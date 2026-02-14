package dwe.holding.customer.client.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.converter.SexTypeConverter;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

// , uniqueConstraints = @UniqueConstraint(name = "uk_parent_name", columnNames = "NAME"))
@Table(name = "CUSTOMER_PET")
@Entity
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
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = SexTypeConverter.class)
    private SexTypeEnum sex;
    private String idealWeight;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;


    // private Set<Visit> visits = new HashSet<Visit>(0);
//    /**
//     *  Estimatespecifics belonging to this Patients (Pets).
//     */
//    private Set<Estimatespecific> estimatespecifics = new HashSet<Estimatespecific>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    @Builder.Default
    private Set<Reminder> reminders = new HashSet<>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    @Builder.Default
    private Set<Note> notepads = new HashSet<>(0);

    @Transient
    public String getNameWithDeceased() {
        return (deceased.name().equals(YesNoEnum.No.name())) ? getName() : getName() + " ✟";
    }

    public String getNameWithDeceasedAndDate() {
        String formattedDate = (getDeceasedDate() != null) ? " (" + getDeceasedDate().format(DateTimeFormatter.ofPattern("dd-MM-yy")) + ")" : "";

        return deceased.equals(YesNoEnum.Yes) ?
                getNameWithDeceased() + formattedDate : getNameWithDeceased();
    }

    @Transient
    // TODO make the years and month variables to be replaced in the string in the controller
    public String getAge() {
        if (deceased.equals(YesNoEnum.Yes)) {
            return getBirthday() == null || getDeceasedDate() == null ? "" : "(" + getBirthday().until(deceasedDate).getYears() + " years " + getBirthday().until(deceasedDate).getMonths() + " months)";
        } else {
            LocalDate today = LocalDate.now();
            return getBirthday() == null ? "" : "(" + getBirthday().until(today).getYears() + " years " + getBirthday().until(today).getMonths() + " months)";
        }
    }
}