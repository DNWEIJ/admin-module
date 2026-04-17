package dwe.holding.customer.client.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.converter.SexTypeConverter;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pet")
    @Builder.Default
    private Set<Reminder> reminders = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pet")
    @Builder.Default
    private Set<Note> notepads = new HashSet<>(0);

    /***********************************/
    public String getPassportNumber() {
        return passportNumber == null ? "" : passportNumber;
    }

    public String getInsuredBy() {
        return insuredBy == null ? "" : insuredBy;
    }

    public String getChipTattooId() {
        return chipTattooId == null ? "" : chipTattooId;
    }

    public String getNameWithDeceased() {
        return (deceased.name().equals(YesNoEnum.No.name())) ? getName() : getName() + " ✟";
    }

    public String getNameWithDeceasedAndDate() {
        String formattedDate = (getDeceasedDate() != null) ? " (" + getDeceasedDate().format(DateTimeFormatter.ofPattern("dd-MM-yy")) + ")" : "";

        return deceased.equals(YesNoEnum.Yes) ?
                getNameWithDeceased() + formattedDate : getNameWithDeceased();
    }

    public String getAgePresentation(String yearsLabel, String monthsLabel) {
        if (deceased.equals(YesNoEnum.Yes)) {
            return getBirthday() == null || getDeceasedDate() == null ? "" :
                    "(" + getBirthday().until(deceasedDate).getYears() + " " + yearsLabel + " " + getBirthday().until(deceasedDate).getMonths() + " " + monthsLabel + ")";
        } else {
            LocalDate today = LocalDate.now();
            return getBirthday() == null ? "" :
                    "(" + getBirthday().until(today).getYears() + " " + yearsLabel + " " + getBirthday().until(today).getMonths() + " " + monthsLabel + ")";
        }
    }

    public boolean hasWarning() {
        return !insured.booleanValue() || gpwarning.booleanValue() || allergies.booleanValue();
    }

    public String getWarningInfoHtml() {
        StringBuilder warning = new StringBuilder();
        if (insured.equals(YesNoEnum.Yes)) {
            warning.append("I: ").append(insuredBy).append('\n');
        } else {
            warning.append("I: ").append('\n');
        }
        if (gpwarning.equals(YesNoEnum.Yes)) {
            warning.append("D: ").append(gpwarningDescription).append('\n');
        } else {
            warning.append("D: ").append('\n');
        }
        if (allergies.equals(YesNoEnum.Yes)) {
            warning.append("A: ").append(allergiesDescription).append('\n');
        } else {
            warning.append("A: ").append('\n');
        }
        return warning.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;
        Pet pet = (Pet) o;
        return getId() != null && Objects.equals(getId(), pet.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Pet(
            Long id,
            String name,
            LocalDate birthday,
            YesNoEnum deceased,
            LocalDate deceasedDate,
            YesNoEnum allergies,
            String allergiesDescription,
            YesNoEnum gpwarning,
            String gpwarningDescription,
            YesNoEnum insured,
            String insuredBy,
            String passportNumber,
            LocalDate chipDate,
            String chipTattooId,
            String briefDescription,
            String species,
            String breed,
            SexTypeEnum sex,
            String idealWeight
    ) {
        this.setId(id);
        this.name = name;
        this.birthday = birthday;
        this.deceased = deceased;
        this.deceasedDate = deceasedDate;
        this.allergies = allergies;
        this.allergiesDescription = allergiesDescription;
        this.gpwarning = gpwarning;
        this.gpwarningDescription = gpwarningDescription;
        this.insured = insured;
        this.insuredBy = insuredBy;
        this.passportNumber = passportNumber;
        this.chipDate = chipDate;
        this.chipTattooId = chipTattooId;
        this.briefDescription = briefDescription;
        this.species = species;
        this.breed = breed;
        this.sex = sex;
        this.idealWeight = idealWeight;
    }
}