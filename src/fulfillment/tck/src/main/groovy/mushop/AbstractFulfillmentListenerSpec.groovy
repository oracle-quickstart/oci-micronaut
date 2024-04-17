/**
 * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.nats.annotation.NatsListener
import io.micronaut.nats.annotation.Subject
import jakarta.inject.Inject
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

abstract class AbstractFulfillmentListenerSpec extends Specification {

    @Inject
    @Client("/fulfillment")
    HttpClient httpClient

    @Inject
    ShipmentListener shipmentListener

    @Inject
    OrdersPublisher ordersPublisher

    void 'test it adds shipment details to order'() {
        given:
        def asyncCond = new AsyncConditions()
        shipmentListener.setAsyncConditions(asyncCond)

        when:
        ordersPublisher.publishOrder(new OrderUpdate(123, null))

        then:
        asyncCond.await()
    }

    @NatsListener
    static class ShipmentListener {
        AsyncConditions asyncConditions

        @Subject("mushop-shipments")
        void handleShipment(OrderUpdate orderUpdate) {
            assert asyncConditions
            asyncConditions.evaluate(() -> {
                assert orderUpdate.shipment
            })
        }
    }
}
