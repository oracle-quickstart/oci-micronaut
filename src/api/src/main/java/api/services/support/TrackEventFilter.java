package api.services.support;

import api.model.Event;
import api.services.EventsService;
import api.services.annotation.TrackEvent;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.web.router.RouteMatch;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import java.util.Collections;


@Filter(Filter.MATCH_ALL_PATTERN)
public class TrackEventFilter implements HttpServerFilter {
    private final EventsService eventsService;

    public TrackEventFilter(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        RouteMatch<?> routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class).orElse(null);
        if (routeMatch != null) {
            final String event = routeMatch.getAnnotationMetadata().stringValue(TrackEvent.class).orElse(null);
            if (event != null) {
                return Flowable.fromPublisher(chain.proceed(request))
                            .onErrorResumeNext(throwable -> {
                                return trackEventError(request, event, throwable);
                            }).flatMap(response ->
                                trackEvent(request, event, response)
                        );
            }
        }
        return chain.proceed(request);
    }

    private Flowable<MutableHttpResponse<?>> trackEventError(HttpRequest<?> request, String event, Throwable throwable) {
        final Session session
                = request.getAttribute(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                .orElse(null);
        if (session != null) {
            Object body = null;
            if (throwable instanceof HttpClientResponseException) {
                final HttpResponse<?> response = ((HttpClientResponseException) throwable).getResponse();
                body = response.body();
            }
            if (body == null){
                body = Collections.singletonMap("message", throwable.getMessage());
            }
            final Event evt = new Event(event + ":error", body);
            return eventsService.trackEvents("api", session.getId(), evt)
                    .onErrorComplete()
                    .andThen(Flowable.error(throwable));
        }
        return Flowable.error(throwable);
    }

    private Flowable<? extends MutableHttpResponse<? extends Object>> trackEvent(
            HttpRequest<?> request,
            String event,
            @Nullable MutableHttpResponse<?> response) {
        final Session session
                = request.getAttribute(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                         .orElse(null);
        if (session != null) {
            final Object body = response != null ? response.body() : null;
            final Event evt = new Event(event, body);
            return eventsService.trackEvents("api", session.getId(), evt)
                    .onErrorComplete()
                    .andThen(Flowable.just(response));
        }
        return Flowable.just(response);
    }
}
