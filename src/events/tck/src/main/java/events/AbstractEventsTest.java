package events;

import events.model.Event;
import events.model.EventRecord;
import events.model.EventsReceived;
import events.service.EventService;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractEventsTest {

    @Inject
    EventsListener eventsListener;

    @Inject
    EventService eventService;

    @Inject
    EventsClient client;

    @Test
    void testEventAndReceive() {
        final Map<String, String> details = Collections.singletonMap("product", "product-001");
        final String source = "client";
        final String track = "abcxyz";
        final String type = "pageView";
        eventService.postEvents(
                source,
                track,
                new Event(type, details)
        );

        assertEventReceived(source, track, type, details);
    }

    @Test
    void testPostEvents() {
        final Map<String, String> details = Collections.singletonMap("product", "product-001");
        final String source = "client";
        final String track = "abcxyz";
        final String type = "pageView";
        System.out.println("Client posting event: " + type);
        final EventsReceived eventsReceived = client.postEvents(
                source,
                track,
                new Event(type, details)
        );

        assertNotNull(eventsReceived);
        assertTrue(eventsReceived.success());
        assertEquals(1, eventsReceived.events());
        assertEventReceived(source, track, type, details);
    }

    private void assertEventReceived(String source, String track, String type, Map<String, String> details) {
        await().atMost(60, SECONDS).until(() -> !eventsListener.received.isEmpty());

        final EventRecord eventRecord = eventsListener.received.stream().findFirst().orElse(null);
        assertNotNull(eventRecord);
        assertEquals(source, eventRecord.source());
        assertEquals(track, eventRecord.track());
        assertNotNull(eventRecord.time());
        assertEquals(type, eventRecord.type());
        assertEquals(details, eventRecord.detail());
    }


    @KafkaListener(offsetReset = OffsetReset.EARLIEST)
    static class EventsListener {
        private final Collection<EventRecord> received = new ConcurrentLinkedDeque<>();

        @Topic("events")
        void receive(EventRecord eventRecord) {
            received.add(eventRecord);
        }
    }

    @Client("/events")
    interface EventsClient {
        @Post(processes = MediaType.APPLICATION_JSON)
        EventsReceived postEvents(
                String source,
                String track,
                Event...events);
    }
}
