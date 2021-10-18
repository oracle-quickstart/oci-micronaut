package api.services.support;

import api.model.Event;
import api.services.EventsService;
import api.services.annotation.TrackEvent;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
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
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * A {@link HttpServerFilter} that intercepts all {@link RouteMatch}s annotated by {@link TrackEvent}. If the annotation
 * {@link TrackEvent} is present the filter sends the tracking event to streaming service.
 */
@Requires(property = "tracking.enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
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
                return Flux.from(chain.proceed(request))
                        .onErrorResume(throwable -> trackEventError(request, event, throwable))
                        .flatMap(response ->
                                trackEvent(request, event, response)
                        );
            }
        }
        return chain.proceed(request);
    }

    private Flux<MutableHttpResponse<?>> trackEventError(HttpRequest<?> request, String event, Throwable throwable) {
        final Session session
                = request.getAttribute(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                .orElse(null);
        if (session != null) {
            Object body = null;
            if (throwable instanceof HttpClientResponseException) {
                final HttpResponse<?> response = ((HttpClientResponseException) throwable).getResponse();
                body = response.body();
            }
            if (body == null) {
                body = Collections.singletonMap("message", throwable.getMessage());
            }
            final Event evt = new Event(event + ":error", body);
            return eventsService.trackEvents("api", session.getId(), evt)
                    .onErrorResume(t -> Mono.empty())
                    .thenMany(Flux.error(throwable));
        }
        return Flux.error(throwable);
    }

    private Flux<? extends MutableHttpResponse<? extends Object>> trackEvent(
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
                    .onErrorResume(throwable -> Mono.empty())
                    .thenMany(response == null ? Flux.empty() : Flux.just(response));
        }
        if (response == null) {
            return Flux.empty();
        } else {
            return Flux.just(response);
        }
    }
}
