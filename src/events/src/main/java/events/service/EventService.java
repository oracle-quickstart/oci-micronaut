package events.service;

import events.model.Event;
import events.model.EventRecord;
import events.model.EventsReceived;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class EventService {
    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);
    private final EventProducer eventProducer;

    public EventService(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @NewSpan("receive events")
    public EventsReceived postEvents(@SpanTag String source, String track, Event...events) {
        final int numEvents = events.length;
        try {
            LOG.debug("Posting Events (source: {}, track {}, length {})", source, track, numEvents);
            if (numEvents == 0) {
                return new EventsReceived(false, 0);
            }

            final EventRecord[] eventRecords = Arrays.stream(events)
                    .map(ev -> new EventRecord(source, track, ev))
                    .toArray(EventRecord[]::new);

            eventProducer.send(eventRecords);
            return new EventsReceived(true, events.length);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Unable to process events: " + e.getMessage(), e);
            }
            return new EventsReceived(false, events.length);
        }
    }
}
