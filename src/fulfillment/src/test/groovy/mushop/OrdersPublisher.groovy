package mushop

import io.micronaut.nats.annotation.NatsClient
import io.micronaut.nats.annotation.Subject

@NatsClient
interface OrdersPublisher {

    @Subject("mushop-orders")
    void publishOrder(OrderUpdate orderUpdate)
}
