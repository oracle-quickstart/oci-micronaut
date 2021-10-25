package events.service;

import events.model.EventRecord;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

/**
 * The messaging Kafka {@link EventRecord} producer.
 *
 * The annotation {@link KafkaClient} is handled by {@link io.micronaut.configuration.kafka.intercept.KafkaClientIntroductionAdvice}
 * which introduces new bean that is able to handle publishing of {@link EventRecord}s to the Kafka.
 */
@KafkaClient(batch = true)
public interface EventProducer {
    String EVENT_TOPIC_NAME = "events";

    @Topic(EVENT_TOPIC_NAME)
    void send(EventRecord... eventRecords);
}
