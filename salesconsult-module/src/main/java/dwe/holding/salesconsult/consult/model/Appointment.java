package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.customer.client.model.Note;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CONSULT_APPOINTMENT",
        indexes = {
                @Index(name = "idx_appointment_member_local_date", columnList = "member_id, local_member_id, visit_date_time, id")
        }
)


@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Appointment extends TenantBaseBO {

    public static final String CANCELLED_COLOUR = "#9999FF";
    public static final String CANCELLED_LABEL_TEXT = "label.visit.status.canceled";
    public static final String FINISHED_COLOUR = "#0A640A";
    public static final String FINISHED_LABEL_TEXT = "label.visit.status.completed";

    @NotNull
    @Column(nullable = false)
    private LocalDateTime visitDateTime;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum cancelled;


    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum completed;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum pickedUp;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum OTC;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "appointment", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Builder.Default
    private Set<Visit> visits = new HashSet<>(0);

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "appointment")
    @Builder.Default
    private Set<LineItem> lineItems = new HashSet<>(0);

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "appointment")
    @Builder.Default
    private Set<Diagnose> diagnoses = new HashSet<>(0);

    @Transient
    public boolean isCancelled() {
        return cancelled.equals(YesNoEnum.Yes);
    }

    @Transient
    public boolean isCompleted() {
        return completed.equals(YesNoEnum.Yes);
    }

    @Transient
    public boolean isPickedUp() {
        return pickedUp.equals(YesNoEnum.Yes);
    }

    @Transient
    public boolean isOTC() {
        return OTC.equals(YesNoEnum.Yes);
    }

    @Transient
    // USED FOR SOAP
    // notes are not related, just on datetime.
    // we will add all notes on the appointment in history:
    // Oldest appointment will contain all notes up and included to this appointment date (and removed from note list)
    // The following appointment will conta all note up and included to this appointment date
    private Set<Note> notes;
}