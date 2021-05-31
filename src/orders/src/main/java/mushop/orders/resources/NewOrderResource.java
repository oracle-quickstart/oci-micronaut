/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package  mushop.orders.resources;

import io.micronaut.core.annotation.Introspected;

import java.net.URI;
import java.util.Objects;
import javax.validation.constraints.NotNull;

@Introspected
public class NewOrderResource {

    @NotNull
    private final URI customer;
    @NotNull
    private final URI address;
    @NotNull
    private final URI card;
    @NotNull
    private final URI items;

    public NewOrderResource(URI customer,
                            URI address,
                            URI card,
                            URI items) {
        this.customer = customer;
        this.address = address;
        this.card = card;
        this.items = items;
    }

    public URI getCustomer() {
        return customer;
    }

    public URI getAddress() {
        return address;
    }

    public URI getCard() {
        return card;
    }

    public URI getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "NewOrderResource{" +
                "\"customer\":" + customer +
                ", \"address\":" + address +
                ", \"card\":" + card +
                ", \"items\": [" + items +
                "] }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewOrderResource that = (NewOrderResource) o;
        return Objects.equals(customer, that.customer) &&
                Objects.equals(address, that.address) &&
                Objects.equals(card, that.card) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, address, card, items);
    }
}
