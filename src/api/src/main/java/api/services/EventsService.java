package api.services;

import api.model.Event;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Completable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Controller("/api")
@Client(id = ServiceLocator.EVENTS)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface EventsService {

    @Operation(
            summary = "Track event",
            description = "Sends track event details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event processed."),
            },
            tags = {"events"}
    )
    @Post("/events")
    Completable trackEvents(String source, String track, Event... events);
}
