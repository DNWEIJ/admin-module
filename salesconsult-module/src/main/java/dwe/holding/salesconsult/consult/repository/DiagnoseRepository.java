package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnoseRepository extends JpaRepository<Diagnose, Long> {

    List<Diagnose> findByMemberId(Long memberId);

    List<Diagnose> findByMemberIdAndPetIdAndAppointmentId(Long memberId, Long petId, Long appointmentId);

    Optional<Diagnose> findByIdAndPetIdAndAppointmentId(Long diagnoseId, Long petId, Long appointmentId);
}