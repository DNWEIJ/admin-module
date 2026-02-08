package dwe.holding.vmas.agenda;

import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.vmas.model.CalendarEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AgendaHelper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public record DataForm(@NotNull LocalDateTime start, @NotNull LocalDateTime end, AgendaTypeEnum agendaType,
                           @NotNull Long localMemberId) {
    }

    public static List<CalendarEvent> fromAppointmentList(List<Appointment> appList, Locale locale, MessageSource messageSource, DataForm dataForm, Set<String> excludedRooms, Set<String> excludedStaff) {
        return appList.stream().map(appointment -> {
                    Map<String, String> map = getTooltipAndBody(appointment, locale, messageSource);
                    return CalendarEvent.fromAppointment(
                            appointment, map.get("tooltip"), map.get("body"), dataForm.agendaType,
                            excludedRooms, excludedStaff
                    );
                }
        ).toList();
    }

    // tooltip:: title="
// |Time: 09:00 - 09:30
// |Owner: Kaspers, F.E.|Phone: (,,0646867402)
// |--------------------------------------------------------|
// Pet: James - Male (Neutered) (Canine,Kruising 10- 25 kg)|
// Purpose: bult op de borst "
// When there are multiple visits on a appointment, we will agregate information in tooltip
    private static Map<String, String> getTooltipAndBody(Appointment appointment, Locale locale, MessageSource messageSource) {

        StringBuilder petTooltipBuilder = new StringBuilder();
        StringBuilder petTextBodyBuilder = new StringBuilder();
        String customerString = null;
        String customerNameId = null;
        Iterator<Visit> it = appointment.getVisits().iterator();
        Visit visit = null;
        int duration = 0;
        while (it.hasNext()) {
            visit = it.next();
            Pet pet = visit.getPet();
            if (!petTooltipBuilder.isEmpty()) {
                petTooltipBuilder.append("- ---  --- -");
            }
            petTextBodyBuilder.append(pet.getNameWithDeceased()).append("-").append(visit.getPurpose()).append("<br/>");

            petTooltipBuilder.append(messageSource.getMessage("label.agenda.pet", new Object[]{
                                    pet.getName(),
                                    messageSource.getMessage(pet.getSex().getLabel(), null, locale),
                                    pet.getSpecies() == null ? "-" : pet.getSpecies(),
                                    pet.getBreed() == null ? "-" : pet.getBreed(),
                            }, locale
                    )
            ).append("\n").append(
                    messageSource.getMessage("label.agenda.purpose", new Object[]{visit.getPurpose()}, locale)
            );
            if (customerString == null) {
                customerString = messageSource.getMessage("label.agenda.owner", new Object[]{
                                pet.getCustomer().getCustomerNameWithId(),
                                pet.getCustomer().getPhoneList()
                        }, locale
                );
                customerNameId = pet.getCustomer().getCustomerNameWithId();
            }
            duration += visit.getEstimatedTimeInMinutes();
        }

        String startTime = appointment.getVisitDateTime().format(TIME_FORMATTER);
        String endTime = appointment.getVisitDateTime().plusMinutes(duration).format(TIME_FORMATTER);

        String timeLine = messageSource.getMessage("label.agenda.time", new Object[]{
                        startTime, endTime
                }, locale
        ) + " (" + visit.getStatus().name().toLowerCase() + ")";


        String toolTip = alignOnFirstColon(String.join("\n", timeLine, customerString, petTooltipBuilder.toString()));
        String bodyText = """
                <div data-tooltip="%s" data-placement="%s">
                <div class="calendar-title"><strong>%s - %s %s</strong><br/></div
                <div class="calendar-body">%s</div>
                </div>
                
                """.formatted(
                toolTip,
                "Top",
                startTime,
                endTime,
                customerNameId + (appointment.isOTC() ? " OTC" : ""),
                petTextBodyBuilder.toString()
        );
        return Map.of(
                "tooltip", toolTip,
                "body", bodyText
        );
    }

    public static String alignOnFirstColon(String input) {
        List<String> lines = input.lines().toList();

        int maxKeyLength = lines.stream()
                .map(String::strip)
                .filter(l -> l.contains(":"))
                .mapToInt(l -> l.indexOf(':'))
                .max()
                .orElse(0);

        return lines.stream()
                .map(l -> {
                    int idx = l.indexOf(':');
                    if (idx < 0) return l;

                    String key = l.substring(0, idx).stripTrailing();
                    String value = StringUtils.trimLeadingWhitespace(l.substring(idx + 1));

                    return key
                            + " ".repeat(maxKeyLength - key.length())
                            + ": "
                            + value;
                })
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }
}
