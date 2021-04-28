package events.controllers;

import events.model.Event;
import events.model.EventsReceived;
import events.service.EventService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Controller("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Submit events endpoint.
     *
     * @param source The source of the event
     * @param track The event to track
     * @param events The events to post
     * @return Whether the events were received or not
     */
    @Post(processes = MediaType.APPLICATION_JSON)
    EventsReceived postEvents(
            @NotBlank String source,
            @NotBlank String track,
            @Min(1) Event...events) {
        return eventService.postEvents(source, track, events);
    }
}
