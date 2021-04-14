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

import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.nats.annotation.NatsListener;
import io.micronaut.nats.annotation.Subject;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.repositories.CustomerOrderRepository;
import mushop.orders.values.OrderUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@NatsListener
public class ShipmentsListener {

    private static final Logger log = LoggerFactory.getLogger(ShipmentsListener.class);

    private final CustomerOrderRepository customerOrderRepository;
    private final MeterRegistry meterRegistry;

    public ShipmentsListener(CustomerOrderRepository customerOrderRepository, MeterRegistry meterRegistry) {
        this.customerOrderRepository = customerOrderRepository;
        this.meterRegistry = meterRegistry;
    }

    @Subject("${mushop.messaging.subjects.shipments}")
    public void handleMessage(OrderUpdate update) {
        Optional<CustomerOrder> customerOrderOptional = customerOrderRepository.findById(update.getOrderId());
        if(customerOrderOptional.isPresent()){
            CustomerOrder order = customerOrderOptional.get();
            log.debug("Updating order {}", order.getId());
            order.setShipment(update.getShipment());
            customerOrderRepository.save(order);
            log.info("order {} is now {}", order.getId(), update.getShipment().getName());
            meterRegistry.counter("orders.fulfillment_ack").increment();
        }else{
            log.error("Order with id {} doesn't exists", update.getOrderId());
        }
    }
}
