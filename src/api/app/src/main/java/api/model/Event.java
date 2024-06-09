package api.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

@Schema(title = "Tracking event")
@Serdeable
public record Event (String type,Object detail,LocalDateTime time){
public Event createWithoutTime(@NonNull String type, Object detail) {

    return new Event(type, detail, null);
}}


