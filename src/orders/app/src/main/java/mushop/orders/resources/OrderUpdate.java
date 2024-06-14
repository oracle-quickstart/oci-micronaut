/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.resources;

import io.micronaut.serde.annotation.Serdeable;
import mushop.orders.entities.Shipment;

/**
 * The messaging business object.
 */
@Serdeable
public class OrderUpdate {

    private final Long orderId;
    private final Shipment shipment;

    public OrderUpdate(Long orderId, Shipment shipment) {
        this.orderId = orderId;
        this.shipment = shipment;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Shipment getShipment() {
        return shipment;
    }

    @Override
    public String toString() {
        return "OrderUpdate{" +
                "orderId='" + orderId + '\'' +
                ", shipment=" + shipment +
                '}';
    }
}
