package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.salesconsult.consult.model.converter.InvoiceStatusConverter;
import dwe.holding.salesconsult.consult.model.converter.VisitStatusConverter;
import dwe.holding.salesconsult.consult.model.type.InvoiceStatusEnum;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CONSULT_VISIT",
        indexes = {
                @Index(name = "idx_visit_appointment_pet", columnList = "appointment_id, pet_id")
        }
)
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Visit extends MemberBaseBO {

    @Builder.Default
    private Double weight = 0.0;
    @Builder.Default
    private Double glucose = 0.0;
    @Builder.Default
    private Double temperature = 0.0;

    @NotNull
    @Column(nullable = false)
    private String purpose;

    private String veterinarian;
    private String room;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = VisitStatusConverter.class)
    @Builder.Default
    private VisitStatusEnum status = VisitStatusEnum.WAITING;

    @Lob
    private String comments;

    @NotNull
    @Column(nullable = false)
    private Integer estimatedTimeInMinutes;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    @Builder.Default
    private YesNoEnum sentToInsurance = YesNoEnum.No;

    // The following three fields are duplicating information from line-items as summations.
    // Using visit instead of line-items will reduce reporting time considerably
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalAmountIncTax = BigDecimal.ZERO;
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalServiceTax = BigDecimal.ZERO;
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalProductTax = BigDecimal.ZERO;


    // TODO move to invoice handling
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = InvoiceStatusConverter.class)
    @Builder.Default
    private InvoiceStatusEnum invoiceStatus = InvoiceStatusEnum.NEW;

    private LocalDate invoiceDate;
    private Long invoiceNumber;
    private LocalDate reminderSendDate;
    private LocalDate reminderSendDate2;
    private LocalDate reminderSendDate3;


    @ManyToOne(fetch = FetchType.EAGER)
    private Pet pet;

    @ManyToOne(fetch = FetchType.EAGER)
    private Appointment appointment;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "visit")
    @Builder.Default
    private Set<PaymentVisit> paymentVisits = new HashSet<>(0);

    @Transient
    public boolean isOpen() {
        return !this.getAppointment().isCancelled() && !this.getAppointment().isCompleted() && VisitStatusEnum.isOpen(this.getStatus());
    }

    /**
     * Required to make thymeleaf -> SpEl happy --> primitives in SpEl are not there
     **/
    public static String getBackgroundColor(Boolean cancelled, Boolean complete, VisitStatusEnum status) {
        return getBackgroundColor(Boolean.TRUE.equals(cancelled), Boolean.TRUE.equals(complete), status);
    }

    public static String getBackgroundColor(boolean cancelled, boolean complete, VisitStatusEnum status) {
        if (cancelled) return Appointment.CANCELLED_COLOUR;
        if (complete) return Appointment.FINISHED_COLOUR;
        return status.getColor();
    }

    public static String getTextColor(String stateHexColor) {
        String hexColor = stateHexColor.replace("#", "");
        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);
        double luminance = 0.299 * r + 0.587 * g + 0.114 * b;
        return luminance > 186 ? "#000000" : "#FFFFFF";
    }

    public String getBackgroundColor() {
        return getBackgroundColor(appointment.isCancelled(), appointment.isCompleted(), status);
    }

    public String getStatusLabel() {
        if (appointment.isCancelled()) return Appointment.CANCELLED_LABEL_TEXT;
        if (appointment.isCompleted()) return Appointment.FINISHED_LABEL_TEXT;
        return status.getLabel();
    }

    public String getTextColor() {
        return getTextColor(getBackgroundColor());
    }
}