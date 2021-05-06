package api.model;

import io.micronaut.core.annotation.Introspected;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Introspected
public class Event {
    private final String type;
    private final Object detail;
    private final LocalDateTime time = LocalDateTime.now();

    public Event(String type, Object detail) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.detail = detail != null ? detail : Collections.emptyMap();
    }

    public String getType() {
        return type;
    }

    public Object getDetail() {
        return detail;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
