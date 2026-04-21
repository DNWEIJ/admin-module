package dwe.holding.reporting.service;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.Reminder;
import dwe.holding.reporting.model.ReportTemplate;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReminderGenerateEmailAndSendOrCreateReport {
    private final GenerateThymeleafService generateService;
    private final SendEmail sendEmail;
    private final ReminderRepository reminderRepository;

    @Async
    public void createHtmlAndSendAsync(ReportTemplate mainTemplate, List<Long> reminders, ReportTemplate template, boolean preview, Counter counter, SessionStorageReporting.ActionType actionType) {
        ResultAndError result = createHtmlAndSend(mainTemplate, reminders, template, preview, counter, actionType);
        // todo push errors to session , use SSE channel to push to the frontend
    }

    public ResultAndError createHtmlAndSend(ReportTemplate mainTemplate, List<Long> reminders, ReportTemplate template, boolean preview, Counter counter, SessionStorageReporting.ActionType actionType) {

        List<String> errorLines = new ArrayList<>();
        List<String> reports = new ArrayList<>();

        reminderRepository.findAllById(reminders).forEach(reminder -> {
            String emailText = generateService.generateFromTemplate(template.getContent(), new DataWrapper(reminder, reminder.getPet(), reminder.getPet().getCustomer()));

            if (preview) {
                reports.add(emailText);
            } else {
                if (SessionStorageReporting.ActionType.REPORT.equals(actionType)) {
                    reports.add(emailText);
                }
                if (SessionStorageReporting.ActionType.EMAIL.equals(actionType)) {
                    if (reminder.getPet().getCustomer().getEmail() != null && !reminder.getPet().getCustomer().getEmail().isEmpty()) {
                        if (sendEmail.sendHtmlEmail(
                                reminder.getPet().getCustomer().getEmail(),
                                template.getSubject(),
                                mainTemplate.getContent().replace("{{emailContentPlaceholder}}", emailText),
                                "noreply@dweholding.nl")
                        ) {
                            reminder.setHasBeenNotified(YesNoEnum.Yes);
                            reminderRepository.save(reminder);
                        } else{
                            errorLines.add("Cannot send email due to error. For " + reminder.getPet().getCustomer().getCustomerNameWithId() + ". Reminder Id: " + reminder.getId());
                        }
                    } else {
                        errorLines.add("Cannot send email because emailaddress is not available. For " + reminder.getPet().getCustomer().getCustomerNameWithId() + ". Reminder Id: " + reminder.getId());
                    }

                }
            }
            counter.increment();
        });
        counter.setFinished(true);
        if (!errorLines.isEmpty()) {
            log.error("email send produced errors: \n" + String.join("\n", errorLines));
        }
        return new ResultAndError(reports, errorLines);
    }

    public record DataWrapper(Reminder reminder, Pet pet, Customer customer) {
    }

    public record ResultAndError(List<String> reports, List<String> errors) {
    }
}
