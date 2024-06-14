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
package mushop.orders.services;

import io.micrometer.core.annotation.Counted;
import io.micronaut.nats.annotation.NatsListener;
import io.micronaut.nats.annotation.Subject;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.resources.OrderUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;

/**
 * Implementation of the <a href="https://micronaut-projects.github.io/micronaut-nats/latest/guide/#consumer">NATS messaging listener</a>.
 * The {@link io.micronaut.nats.intercept.NatsConsumerAdvice} is an ExecutableMethodProcessor that will process all
 * beans annotated with NatsListener. It creates and subscribes the relevant methods as consumers to Nats subjects.
 */
@NatsListener
public class ShipmentsListener {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentsListener.class);

    private final CustomerOrderRepository customerOrderRepository;

    public ShipmentsListener(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    @Subject("mushop-shipments")
    @Transactional
    @Counted("orders.fulfilled")
    public void handleMessage(OrderUpdate update) {
        LOG.debug("Received order update {}", update);
        CustomerOrder order = customerOrderRepository.findById(update.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + update.getOrderId() + "  doesn't exist"));
        order.setShipment(update.getShipment());
        customerOrderRepository.save(order);
        LOG.debug("order {} is now {}", order.getId(), update.getShipment().getName());
    }
}
