package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;


public record VisitProjection (
    Long id,
    Long petId,
    Long appointmentId,
    @Value("#{target.appointment.OTC}")
    YesNoEnum appointmentOTC,
    LocalDateTime appointmentVisitDateTime,
    Long appointmentLocalMemberId,
    String purpose,
    VisitStatusEnum status) {}