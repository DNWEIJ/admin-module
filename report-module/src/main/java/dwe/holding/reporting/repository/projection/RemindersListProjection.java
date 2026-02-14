package dwe.holding.reporting.repository.projection;

import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
public class RemindersListProjection {

//    public final Long appointmentId;
//    public final LocalDateTime visitDateTime;
//    public final YesNoEnum cancelled;
//    public final YesNoEnum completed;
//    public final YesNoEnum otc;
//
//    public final Long visitId;
//    public final Integer estimatedTimeInMinutes;
//    public final String veterinarian;
//    public final String purpose;
//    public final String room;
//    public final VisitStatusEnum status;
//    public final YesNoEnum sentToInsurance;

    public final Long petId;
    public final String petName;
    public final String species;
    public final String breed;
//    public final YesNoEnum insured;
//    public final String insuredBy;

    public final Long customerId;
    public final String lastName;
    public final String firstName;
    public final String surName;
    public final String middleInitial;
    public final String email;
    public final String mobilePhone;
    public final String workPhone;

    public final LocalDate dueDate;
    public final String reminderText;
    public final Long reminderId;
    public final Long originatingAppointmentId;
}