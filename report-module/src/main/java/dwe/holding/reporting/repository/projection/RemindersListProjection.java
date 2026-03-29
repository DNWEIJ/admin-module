package dwe.holding.reporting.repository.projection;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class RemindersListProjection {
    public final Long petId;
    public final String petName;
    public final String species;
    public final String breed;

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