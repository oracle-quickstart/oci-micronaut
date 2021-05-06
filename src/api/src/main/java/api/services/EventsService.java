package api.services;

import api.model.Event;
import api.services.annotation.MuService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Controller("/api")
@Client(id = ServiceLocator.EVENTS)
@Secured(SecurityRule.IS_ANONYMOUS)
public interface EventsService {
    @Post("/events")
    Completable trackEvents(String source, String track, Event... events);
}
