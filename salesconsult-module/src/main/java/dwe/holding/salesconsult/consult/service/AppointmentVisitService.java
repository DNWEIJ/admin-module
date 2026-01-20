package dwe.holding.salesconsult.consult.service;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.InvoiceStatusEnum;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AppointmentVisitService {
    private final AppointmentRepository appointmentRepository;

    private final CustomerService customerService;

    public Appointment createAppointmentVisit(List<CreatePet> pets, Long customerId, SalesType salesType) {

        Appointment appointment = Appointment.builder()
                .OTC(salesType.isOtc() ? YesNoEnum.Yes : YesNoEnum.No)
                .cancelled(YesNoEnum.No)
                .pickedUp(YesNoEnum.No)
                .completed(YesNoEnum.No)
                .visitDateTime(LocalDateTime.now())
                .localMemberId(AutorisationUtils.getCurrentUserMlid())
                .build();
        appointment.setVisits(
                pets.stream().map(formPet ->
                        Visit.builder()
                                .id(null)
                                .version(null)
                                .appointment(appointment)
                                .pet(customerService.getPet(customerId, formPet.id()))
                                .room(formPet.room())
                                .veterinarian(formPet.vet())
                                .purpose(formPet.purpose())
                                .estimatedTimeInMinutes(formPet.timeNeeded().isEmpty() ? 0 : Integer.parseInt(formPet.timeNeeded()))
                                .veterinarian(AutorisationUtils.getCurrentUserAccount())
                                .status(VisitStatusEnum.WAITING)
                                .sentToInsurance(YesNoEnum.No)
                                .invoiceStatus(InvoiceStatusEnum.NEW)
                                .build()
                ).collect(Collectors.toSet())
        );
        return appointmentRepository.save(appointment);
    }

    public Appointment addPetsToAppointment(Long customerId, List<CreatePet> pets, Appointment app) {
        app.getVisits().addAll(
                pets.stream().filter(pet -> pet.checked() != null).map(formPet ->
                        Visit.builder()
                                .appointment(app)
                                .pet(customerService.getPet(customerId, formPet.id()))
                                .room("")
                                .purpose(formPet.purpose() == null ? "" : formPet.purpose())
                                .estimatedTimeInMinutes(5)
                                .veterinarian(AutorisationUtils.getCurrentUserAccount())
                                .status(VisitStatusEnum.WAITING)
                                .sentToInsurance(YesNoEnum.No)
                                .invoiceStatus(InvoiceStatusEnum.NEW)
                                .build()
                ).collect(Collectors.toSet()));
        Appointment savedApp = appointmentRepository.save(app);
        return savedApp;
    }

    @Transactional
    public void saveAppointment(Long appointmentId, LocalDateTime visitDateTime, Long localMemberId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointment.setVisitDateTime(visitDateTime);
        appointment.setLocalMemberId(localMemberId);
        appointmentRepository.save(appointment);
    }


    public record CreatePet(@NotNull Long id, Boolean checked, String purpose, String timeNeeded, String vet, String room) {
        public CreatePet {
            checked = checked == null ? false : checked;
            purpose = purpose == null ? "" : purpose;
            timeNeeded = timeNeeded == null ? "" : timeNeeded;
            vet = vet == null ? "" : vet;
            room = room == null ? "" : room;
        }
    }
}