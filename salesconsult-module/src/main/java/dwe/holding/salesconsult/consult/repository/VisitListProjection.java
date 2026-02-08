package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
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

        public final Long petId;
        public final String petName;
        public final String species;
        public final String breed;

        public final Long customerId;
        public final String lastName;
        public final String firstName;
        public final String surName;
        public final String middleInitial;

        public final BigDecimal totalAmount;
        public final BigDecimal  paidAmount;
}