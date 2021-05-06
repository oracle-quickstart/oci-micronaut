/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.resources;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;

import java.util.Objects;

@Introspected
public class PaymentRequest {
    private Address address;
    private Card card;
    private Customer customer;
    private float amount;

    public PaymentRequest() {
    }

    @Creator
    public PaymentRequest(Address address, Card card, Customer customer, float amount) {
        this.address = address;
        this.customer = customer;
        this.card = card;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "address=" + address +
                ", card=" + card +
                ", customer=" + customer +
                ",amount=" + amount +
                '}';
    }

    public Address getAddress() {
        return address;
    }

    public PaymentRequest setAddress(Address address) {
        this.address = address;
        return this;
    }

    public Card getCard() {
        return card;
    }

    public PaymentRequest setCard(Card card) {
        this.card = card;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public PaymentRequest setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequest that = (PaymentRequest) o;
        return Float.compare(that.amount, amount) == 0 &&
                Objects.equals(address, that.address) &&
                Objects.equals(card, that.card) &&
                Objects.equals(customer, that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, card, customer, amount);
    }
}
