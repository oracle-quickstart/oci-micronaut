package events;

import events.model.Event;
import events.model.EventRecord;
import events.model.EventsReceived;
import events.service.EventService;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventsTest implements TestPropertyProvider {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

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
        final EventsReceived eventsReceived = client.postEvents(
                source,
                track,
                new Event(type, details)
        );


        assertNotNull(eventsReceived);
        assertTrue(eventsReceived.isSuccess());
        assertEquals(1, eventsReceived.getEvents());
        assertEventReceived(source, track, type, details);
    }

    private void assertEventReceived(String source, String track, String type, Map<String, String> details) {
        await().atMost(30, SECONDS).until(() -> !eventsListener.received.isEmpty());

        final EventRecord eventRecord = eventsListener.received.stream().findFirst().orElse(null);
        assertNotNull(eventRecord);
        assertEquals(
                source,
                eventRecord.getSource()
        );
        assertEquals(
                track,
                eventRecord.getTrack()
        );
        assertNotNull(
                eventRecord.getTime()
        );
        assertEquals(
                type,
                eventRecord.getType()
        );
        assertEquals(
                details,
                eventRecord.getDetail()
        );
    }

    @NonNull
    @Override
    public Map<String, String> getProperties() {
        return Collections.singletonMap(
                "kafka.bootstrap.servers", kafka.getBootstrapServers()
        );
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
