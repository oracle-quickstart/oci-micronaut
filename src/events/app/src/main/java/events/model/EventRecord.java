package events.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Event record.
 */
// import java.time.Instant;
// import java.util.Map;
// import java.util.Objects;

@Serdeable
public record EventRecord(String source, String track, Instant time, Event event) {

    public EventRecord(String source, String track, Event event) {
        this(source, track, Instant.now(), Objects.requireNonNull(event, "Event cannot be null"));
    }

    @Creator
    public EventRecord(String source, String track, String type, Map<String, String> detail) {
        this(source, track, Instant.now(), new Event(type, detail));
    }

    public String type() {
        return event.type();
    }

    public Map<String, String> detail() {
        return event.detail();
    }
}
