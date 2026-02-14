package dwe.holding.salesconsult.consult;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Note;
import dwe.holding.customer.client.repository.NoteRepository;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SoapAndHistoryService {
    private final VisitRepository visitRepository;
    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<Appointment> getSoap(Long petId) {
        List<Visit> visits = visitRepository.findByMemberIdAndPet_Id(AutorisationUtils.getCurrentUserMid(), petId);
        visits.stream().forEach(v -> {
            v.getAppointment().getLineItems().size();
            v.getAppointment().getDiagnoses().size();
        });
        // change from visits -> appointment with visits
        Map<Appointment, Set<Visit>> grouped =
                visits.stream().collect(Collectors.groupingBy(Visit::getAppointment, Collectors.toSet()));
        grouped.forEach((appointment, visitSet) -> {
            appointment.getVisits().clear();
            appointment.getVisits().addAll(visitSet);
        });
        List<Appointment> appointmentList =
                grouped.keySet()
                        .stream()
                        .sorted(Comparator.comparing(Appointment::getVisitDateTime).reversed())
                        .toList();

        List<Note> noteList = noteRepository.findByPet_Id(petId);
        int lastIndex = appointmentList.size() - 1;

        // move notes to appointment
        for (int iteratorCount = 0; iteratorCount < appointmentList.size(); iteratorCount++) {
            Appointment appt = appointmentList.get(iteratorCount);
            boolean isLast = iteratorCount == lastIndex;
            boolean isFirst = iteratorCount == 0;

            // iterate through all notes per appointment and use a new list
            Iterator<Note> noteIt = noteList.iterator();
            appt.setNotes(new HashSet<>());

            while (noteIt.hasNext()) {
                Note note = noteIt.next();
                LocalDate visitDate = appt.getVisitDateTime().toLocalDate();

                // going from today back with the appointmentList
                if (isFirst && note.getNoteDate().isAfter(visitDate)) {
                    appt.getNotes().add(note);
                    noteIt.remove();
                } else if (isLast && note.getNoteDate().isBefore(visitDate)) {
                    appt.getNotes().add(note);
                    noteIt.remove();
                } else if (note.getNoteDate().isEqual(visitDate) || note.getNoteDate().isAfter(visitDate)) {
                    appt.getNotes().add(note);
                    noteIt.remove();
                }
            }
        }
        return appointmentList;
    }


    public List<HistoryProduct> getHistoryForProducts(Long petId) {
        return visitRepository.findByMemberIdAndPet_Id(AutorisationUtils.getCurrentUserMid(), petId)
                .stream().flatMap(visit -> HistoryProduct.fromVisit(visit).stream())
                .sorted(Comparator.comparing(HistoryProduct::visitDate).reversed())
                .toList();
    }

    public List<History> getHistoryForTempWeightClugose(Long petId) {
        return visitRepository.findByMemberIdAndPet_Id(AutorisationUtils.getCurrentUserMid(), petId)
                .stream()
                .filter(v -> !v.getAppointment().isOTC())
                .map(History::fromVisit)
                .sorted(Comparator.comparing(History::visitDate).reversed())
                .toList();
    }

    public List<HistoryDiagnose> getHistoryForDiagnose(Long petId) {

        return visitRepository.findByMemberIdAndPet_Id(AutorisationUtils.getCurrentUserMid(), petId)
                .stream().flatMap(visit -> HistoryDiagnose.fromVisit(visit).stream())
                .sorted(Comparator.comparing(HistoryDiagnose::visitDate).reversed())
                .toList();

    }


    public record HistoryDiagnose(
            LocalDate visitDate,
            String diagnose,
            String location
    ) {
        public static List<HistoryDiagnose> fromVisit(Visit visit) {
            return visit.getAppointment().getDiagnoses().stream().map(diagnose ->
                    new HistoryDiagnose(
                            visit.getAppointment().getVisitDateTime().toLocalDate(),
                            diagnose.getLookupDiagnose().getNomenclature(),
                            diagnose.getLookupLocation().getNomenclature()
                    )).toList();
        }
    }

    public record HistoryProduct(
            LocalDate visitDate,
            Long categoryId,
            String nomenclature,
            BigDecimal quantity
    ) {
        public static List<HistoryProduct> fromVisit(Visit visit) {
            return visit.getAppointment().getLineItems().stream().map(lineItem ->
                    new HistoryProduct(
                            visit.getAppointment().getVisitDateTime().toLocalDate(),
                            lineItem.getCategoryId(),
                            lineItem.getNomenclature(),
                            lineItem.getQuantity()
                    )).toList();
        }
    }

    public record History(
            LocalDate visitDate,
            Double weight,
            Double glucose,
            Double temperature
    ) {
        public static History fromVisit(Visit visit) {
            return new History(
                    visit.getAppointment().getVisitDateTime().toLocalDate(),
                    Objects.requireNonNullElse(visit.getWeight(), (double) 0),
                    Objects.requireNonNullElse(visit.getGlucose(), (double) 0),
                    Objects.requireNonNullElse(visit.getTemperature(), (double) 0)
            );
        }
    }
}