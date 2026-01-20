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
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CONSULT_VISIT")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Visit extends MemberBaseBO {

    private Double weight;
    private Double glucose;
    private Double temperature;

    @NotNull
    @Column(nullable = false)
    private String veterinarian;

    @NotNull
    @Column(nullable = false)
    private String purpose;

    @NotNull
    @Column(nullable = false)
    private String room;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = VisitStatusConverter.class)
    private VisitStatusEnum status;

    @Lob
    private String comments;

    @NotNull
    @Column(nullable = false)
    private Integer estimatedTimeInMinutes;

    // TODO move to invoice handling
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = InvoiceStatusConverter.class)
    private InvoiceStatusEnum invoiceStatus;
    private LocalDate invoiceDate;
    private Long invoiceNumber;
    private LocalDate reminderSendDate;
    private LocalDate reminderSendDate2;
    private LocalDate reminderSendDate3;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum sentToInsurance;

    @ManyToOne(fetch = FetchType.EAGER)
    private Pet pet;

    @ManyToOne(fetch = FetchType.EAGER)
    private Appointment appointment;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "visit")
    @Builder.Default
    private Set<PaymentVisit> paymentVisits = new HashSet<>(0);

    @Transient
    public @Nullable boolean isOpen() {
        return !this.getAppointment().isCancelled() && !this.getAppointment().iscompleted()  && VisitStatusEnum.isOpen(this.getStatus());

    }
}