package events.service;

import events.model.EventRecord;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient(batch = true)
public interface EventProducer {
    String EVENT_TOPIC_NAME = "events";

    @Topic(EVENT_TOPIC_NAME)
    void send(EventRecord... eventRecords);
}
