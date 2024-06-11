package api.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Tracking event")
@Serdeable
public record Event (String type,Object detail){
}


