package mushop.orders.services;

import io.micronaut.nats.annotation.NatsClient;
import io.micronaut.nats.annotation.Subject;
import io.reactivex.Single;
import mushop.orders.resources.OrderUpdate;

@NatsClient
public interface OrdersPublisher {

    @Subject("mushop-orders")
    Single<OrderUpdate> dispatchToFulfillment(OrderUpdate orderUpdate);
}
