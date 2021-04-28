package events.service;

import events.model.EventRecord;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient(batch = true)
public interface EventProducer {
    @Topic("events")
    void send(EventRecord... eventRecords);
}
