package dwe.holding.reporting.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.reporting.repository.dsl.ReminderListDsl;
import dwe.holding.reporting.repository.projection.RemindersListProjection;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.comparing;

@RequestMapping("report")
@AllArgsConstructor
@Controller
public class RemindersReportController {
    private final ReminderListDsl reminderListDsl;
    private final ReminderRepository reminderRepository;
    private final LookupSpeciesRepository lookupSpeciesRepository;

    // TODO move to customer?

    @GetMapping("reminders")
    public String reportReminders(Model model, ReminderListForm reminderForm) {
        if (reminderForm == null || reminderForm.from == null) {
            reminderForm = getForm();
        }

        model
                .addAttribute("reminderForm", reminderForm)
                .addAttribute("reminderTypes", reminderRepository.findDistinctReminderTextByMemberIdOrderByReminderText(AutorisationUtils.getCurrentUserMid())
                        .stream().map(reminder -> new PresentationElement(reminder, reminder, true)).toList())
                .addAttribute("species", lookupSpeciesRepository.findByMemberIdIn(List.of(AutorisationUtils.getCurrentUserMid(), -1L))
                        .stream().map(f -> new PresentationElement(f.getId(), f.getSpecies()))
                        .sorted(comparing(PresentationElement::getName)).toList())
                .addAttribute("reminders", getReminders(reminderForm))
        ;
        return "reporting-module/reminders";
    }


    ReminderListForm getForm() {
        return new ReminderListForm(LocalDate.now(), LocalDate.now().plusDays(1),
                true, "", "all", List.of(), 0, List.of(), 0
        );
    }

    List<RemindersListProjection> getReminders(ReminderListForm reminderForm) {

        return reminderListDsl.findReminders(AutorisationUtils.getCurrentUserMid(),
                reminderForm.from, reminderForm.includeTill,
                (reminderForm.species.size() == reminderForm.speciesListLength) ? List.of() : reminderForm.species,
                (reminderForm.reminderTypes.size() == reminderForm.speciesListLength) ? List.of() : reminderForm.reminderTypes,
                reminderForm.dueDateType, reminderForm.LastNameFilter, reminderForm.onlyActiveWithLivePet);
    }

    public record ReminderListForm(
            LocalDate from, LocalDate includeTill,
            Boolean onlyActiveWithLivePet, String LastNameFilter, String dueDateType,
            List<String> species, Integer speciesListLength, List<String> reminderTypes, Integer reminderTypesLength) {
    }
}