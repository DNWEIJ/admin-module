package dwe.holding.vmas.model;

import java.util.Map;

public record CalendarResource(

        // Unique identifier of the resource
        String id,

        // Title of the resource (usually text)
        String title,

        // Default background color for events assigned to this resource
        String eventBackgroundColor,

        // Default text color for events assigned to this resource
        String eventTextColor,

        // Arbitrary custom properties
        Map<String, Object> extendedProps

) {
}

