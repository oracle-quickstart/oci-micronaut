package events.model;

import io.micronaut.serde.annotation.Serdeable;


import java.util.Map;


/**
 * The event to track.
 */
@Serdeable
public record Event(String type,Map<String, String> detail){
}

