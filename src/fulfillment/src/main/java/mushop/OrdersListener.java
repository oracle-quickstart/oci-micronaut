/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mushop;

import io.micrometer.core.annotation.Counted;
import io.micronaut.nats.annotation.NatsListener;
import io.micronaut.nats.annotation.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@NatsListener
public class OrdersListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersListener.class);

    private final ShipmentsPublisher shipmentsPublisher;

    public OrdersListener(ShipmentsPublisher shipmentsPublisher) {
        this.shipmentsPublisher = shipmentsPublisher;
    }

    @Subject("mushop-orders")
    @Counted(value = "orders.received")
    public void handleMessage(OrderUpdate orderUpdate) {
        LOG.info("got message {} on the mushop orders subject", orderUpdate);
        Shipment shipment = new Shipment(UUID.randomUUID().toString(), "Shipped");
        orderUpdate.setShipment(shipment);
        LOG.info("Sending shipment update {}", orderUpdate);
        shipmentsPublisher.publishShipment(orderUpdate);
    }
}
