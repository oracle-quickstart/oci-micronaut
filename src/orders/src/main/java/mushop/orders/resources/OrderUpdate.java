/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.resources;

import io.micronaut.core.annotation.Introspected;
import mushop.orders.entities.Shipment;

@Introspected
public class OrderUpdate {
    private final Long orderId;
    private final mushop.orders.entities.Shipment Shipment;

    public OrderUpdate(Long orderId, Shipment shipment) {
        this.orderId = orderId;
        Shipment = shipment;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Shipment getShipment() {
        return Shipment;
    }

    @Override
    public String toString() {
        return "OrderUpdate{" +
                "orderId='" + orderId + '\'' +
                ", Shipment=" + Shipment +
                '}';
    }
}
