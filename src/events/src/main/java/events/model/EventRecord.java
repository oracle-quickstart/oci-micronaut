package events.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Introspected
public class EventRecord extends Event {
    private final String source;
    private final String track;
    private final Instant time;

    public EventRecord(String source, String track, Event event) {
        this(source, track, Objects.requireNonNull(event, "Event cannot be null").getType(), event.getDetail());
    }

    @Creator
    public EventRecord(String source, String track, String type, Map<String, String> detail) {
        super(type, detail);
        this.source = source;
        this.track = track;
        this.time = Instant.now();
    }

    public String getSource() {
        return source;
    }

    public String getTrack() {
        return track;
    }

    public Instant getTime() {
        return time;
    }
}
