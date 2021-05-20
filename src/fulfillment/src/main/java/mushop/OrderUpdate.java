/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package  mushop;

public class OrderUpdate {

    private Long orderId;
    private Shipment shipment;

    public OrderUpdate() {
    }

    public OrderUpdate(Long orderId, Shipment shipment) {
        this.orderId = orderId;
        this.shipment = shipment;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @Override
    public String toString() {
        return "OrderUpdate{" +
                "orderId='" + orderId + '\'' +
                ", shipment=" + shipment +
                '}';
    }
}
