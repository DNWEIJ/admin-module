package dwe.holding.model;

import java.time.Instant;

public record SseEvent(
        SseEventType type,
        Instant timestamp,
        Object payload
) {}
