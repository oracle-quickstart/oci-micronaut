/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Cart implements Serializable {

    @NotNull
    @ManyToOne
    private Customer customer;

    @Id
    private String id;

    @OneToMany
    private List<Item> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(@NotNull Customer customer, String id, List<Item> items) {
        this.customer = customer;
        this.id = id;
        this.items = items;
    }

    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cart cart = (Cart) o;
        return Objects.equals(customer, cart.customer) &&
                Objects.equals(id, cart.id) &&
                Objects.equals(items, cart.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, id, items);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "customer=" + customer +
                ", id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}
