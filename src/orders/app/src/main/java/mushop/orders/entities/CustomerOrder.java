/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
// import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import io.micronaut.serde.annotation.Serdeable;

/**
 * The customer address business object.
 */
@Serdeable
@Entity
public class CustomerOrder {

    @JsonProperty("orderId")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @Nullable
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @Nullable
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @Nullable
    private Card card;

    @OneToMany(cascade = CascadeType.ALL)
    @Nullable
    private Collection<Item> items;

    @OneToOne(cascade = CascadeType.ALL)
    @Nullable
    private Shipment shipment;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateCreated
    private Date orderDate = Calendar.getInstance().getTime();

    private Float total;

    public CustomerOrder() {
    }

    public CustomerOrder(Long id,
                         Customer customer,
                         Address address,
                         Card card,
                         List<Item> items,
                         Shipment shipment,
                         Date orderDate,
                         Float total) {
        super();
        this.id = id;
        this.customer = customer;
        this.address = address;
        this.card = card;
        this.items = items;
        this.shipment = shipment;
        this.orderDate = orderDate;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Nullable
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Nullable
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Nullable
    public Collection<Item> getItems() {
        return items;
    }

    public void setItems(Collection<Item> items) {
        this.items = items;
    }

    @Nullable
    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerOrder that = (CustomerOrder) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(customer, that.customer) &&
                Objects.equals(address, that.address) &&
                Objects.equals(card, that.card) &&
                Objects.equals(items, that.items) &&
                Objects.equals(shipment, that.shipment) &&
                Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, address, card, items, shipment, orderDate);
    }

    @Override
    public String toString() {
        return "CustomerOrder [id=" + id +
                ", customer=" + customer +
                ", address=" + address +
                ", card=" + card +
                ", items=" + items +
                ", shipment=" + shipment +
                ", orderDate=" + orderDate +
                ", total=" + total +
                "]";
    }
}
