package dwe.holding.vmas.model;

import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CalendarEvent(

        // A unique string identifier of the event
        String id,

        // An array of resource IDs associated with the event
        List<String> resourceIds,

        // Whether the event is shown in the all-day slot
        boolean allDay,

        // JavaScript Date -> represented as an instant in JSON
        Instant start,
        Instant end,

        // Content (library allows richer content, backend usually sends text)
        String title,

        // Editable overrides (nullable because JS allows undefined)
        boolean editable,
        boolean startEditable,
        boolean durationEditable,

        // "auto", "background" (others appear only in callbacks)
        String display,

        // Style overrides
        String backgroundColor,
        String textColor,

        // Additional CSS class names
        List<String> classNames,

        // Inline style declarations (array, not object)
        List<String> styles,

        // Arbitrary custom properties
        Map<String, Object> extendedProps

) {


    public static CalendarEvent fromAppointment(Appointment appointment, String toolTip, String bodyText, AgendaTypeEnum type, Set<String> excludedRooms, Set<String> excludedStaff) {

        Map<String, Object> extendedProps = new HashMap<>();
        extendedProps.put("tooltip", toolTip);
        extendedProps.put("bodyText", bodyText);
        extendedProps.put("type", type);
        extendedProps.put("contextStatus", getContextStatus(appointment));


        Visit visit = appointment.getVisits().iterator().next();
        extendedProps.put("visitId", visit.getId());
        extendedProps.put("customerId", visit.getPet().getCustomer().getId());

        List<String> resource = (appointment.getVisits().size() > 1) ?
                List.of(AgendaTypeEnum.Vet.equals(type) ? visit.getVeterinarian() : visit.getRoom())
                :
                List.of(getResource(visit, type, excludedRooms, excludedStaff));

        return new CalendarEvent(appointment.getId().toString(), resource,
                false,
                appointment.getVisitDateTime().atZone(ZoneId.systemDefault()).toInstant(),
                getEnd(appointment),
                "", false, true, false, "auto", visit.getBackgroundColor(), visit.getTextColor(), null, null,
                extendedProps);
    }

    private static String getResource(Visit visit, AgendaTypeEnum type, Set<String> excludedRooms, Set<String> excludedStaff) {
        if (AgendaTypeEnum.Vet.equals(type)) {
            if (excludedStaff.contains(visit.getVeterinarian())) {
                return "other";
            }
            return visit.getVeterinarian();
        } else {
            if (excludedRooms.contains(visit.getRoom())) {
                return "other";
            } else {
                return visit.getRoom();
            }
        }
    }

    private static String getContextStatus(Appointment appointment) {
        if (appointment.isCancelled()) return "no-context";
        if (appointment.isCompleted()) return "no-context";
        VisitStatusEnum status = appointment.getVisits().iterator().next().getStatus();
        if (VisitStatusEnum.PLANNED.equals(status)) {
            return "waiting";
        }
        if (VisitStatusEnum.WAITING.equals(status)) {
            return "planned";
        }
        return "no-context";
    }

    private static Instant getEnd(Appointment appointment) {
        return appointment.getVisitDateTime().plusMinutes(
                appointment.getVisits().stream().mapToInt(Visit::getEstimatedTimeInMinutes).sum()
        ).atZone(ZoneId.systemDefault()).toInstant();
    }
}


