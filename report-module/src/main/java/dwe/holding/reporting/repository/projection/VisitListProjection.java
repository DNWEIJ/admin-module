package dwe.holding.reporting.repository.projection;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class VisitListProjection {
    public final Long appointmentId;
    public final LocalDateTime visitDateTime;
    public final YesNoEnum cancelled;
    public final YesNoEnum completed;
    public final YesNoEnum otc;

    public final Long visitId;
    public final Integer estimatedTimeInMinutes;
    public final String veterinarian;
    public final String purpose;
    public final String room;
    public final VisitStatusEnum status;
    public final YesNoEnum sentToInsurance;

    public final Long petId;
    public final String petName;
    public final String species;
    public final String breed;
    public final YesNoEnum insured;
    public final String insuredBy;

    public final Long customerId;
    public final String lastName;
    public final String firstName;
    public final String surName;
    public final String middleInitial;
    public final BigDecimal totalAmountIncTax;
    public BigDecimal paidAmount;

    // 2nd constructor for LocalDate -> LocalDateTime conversion
    public VisitListProjection(
            Long appointmentId,
            LocalDate visitDate,
            YesNoEnum cancelled,
            YesNoEnum completed,
            YesNoEnum otc,

            Long visitId,
            Integer estimatedTimeInMinutes,
            String veterinarian,
            String purpose,
            String room,
            VisitStatusEnum status,
            YesNoEnum sentToInsurance,

            Long petId,
            String petName,
            String species,
            String breed,
            YesNoEnum insured,
            String insuredBy,

            Long customerId,
            String lastName,
            String firstName,
            String surName,
            String middleInitial,
            BigDecimal totalAmountIncTax,
            BigDecimal paidAmount
    ) {
        this.appointmentId = appointmentId;
        this.visitDateTime = visitDate.atStartOfDay();
        this.cancelled = cancelled;
        this.completed = completed;
        this.otc = otc;
        this.visitId = visitId;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.veterinarian = veterinarian;
        this.purpose = purpose;
        this.room = room;
        this.status = status;
        this.sentToInsurance = sentToInsurance;
        this.petId = petId;
        this.petName = petName;
        this.species = species;
        this.breed = breed;
        this.insured = insured;
        this.insuredBy = insuredBy;
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.surName = surName;
        this.middleInitial = middleInitial;
        this.totalAmountIncTax = totalAmountIncTax;
        this.paidAmount = paidAmount;
    }

    public boolean isCancelled() {
        return cancelled.equals(YesNoEnum.Yes);
    }

    public boolean isCompleted() {
        return completed.equals(YesNoEnum.Yes);
    }

    // copied from visit
    public String getStatusLabel() {
        if (this.isCancelled()) return Appointment.CANCELLED_LABEL_TEXT;
        if (this.isCompleted()) return Appointment.FINISHED_LABEL_TEXT;
        return status.getLabel();
    }

}