package api.services;

import api.model.Event;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Completable;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api")
@Client(id = ServiceLocator.EVENTS)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface EventsService {

    /**
     * Tracks an event.
     *
     * @param source The source of the event
     * @param track The event to track
     * @param events The event objects
     */
    @Tag(name="events")    
    @ApiResponse(responseCode = "200", description = "Event processed.")
    @Post("/events")
    Completable trackEvents(String source, String track, Event... events);
}
