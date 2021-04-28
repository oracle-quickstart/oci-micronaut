package events.model;

import io.micronaut.core.annotation.Introspected;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Introspected
public class Event {
    private final String type;
    private final Map<String, String> detail;

    public Event(String type, Map<String, String> detail) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.detail = detail != null ? detail : Collections.emptyMap();
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getDetail() {
        return detail;
    }
}
