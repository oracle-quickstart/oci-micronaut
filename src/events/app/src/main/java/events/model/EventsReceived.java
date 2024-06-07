package events.model;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Defines whether events were received.
 */
@Serdeable
public record EventsReceived(boolean success ,int events){
}

