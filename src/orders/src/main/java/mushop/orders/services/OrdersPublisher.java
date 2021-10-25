package mushop.orders.services;

import io.micronaut.nats.annotation.NatsClient;
import io.micronaut.nats.annotation.Subject;
import mushop.orders.resources.OrderUpdate;

/**
 * The implementation of NATS messaging publisher. It leveregase the {@link NatsClient} that in combination with
 * {@link io.micronaut.nats.intercept.NatsIntroductionAdvice} generates a bean that handles publishing of messages.
 */
@NatsClient
public interface OrdersPublisher {

    @Subject("mushop-orders")
    void dispatchToFulfillment(OrderUpdate orderUpdate);
}
