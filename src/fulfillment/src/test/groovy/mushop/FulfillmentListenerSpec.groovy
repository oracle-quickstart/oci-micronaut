/**
 * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop

import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.nats.annotation.NatsListener
import io.micronaut.nats.annotation.Subject
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.util.concurrent.AsyncConditions

import javax.inject.Inject

@MicronautTest
class FulfillmentListenerSpec extends AbstractFulfillmentSpec {

    @Inject
    @Client("/fulfillment")
    RxHttpClient httpClient

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
