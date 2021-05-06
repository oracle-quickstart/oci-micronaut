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
package mushop.orders.controllers.dto;


import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.hateoas.AbstractResource;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Shipment;

import java.util.Collection;
import java.util.Date;

@Introspected
public class CustomerOrderDto extends AbstractResource<CustomerOrderDto> {

    private Long id;

    private final CustomerDto customer;

    private final Address address;

    private final Card card;

    private final Collection<ItemDto> items;

    private final Shipment shipment;

    private final Date orderDate;

    private final Float total;

    public CustomerOrderDto(CustomerDto customer, Address address, Card card, Collection<ItemDto> items, Shipment shipment, Date orderDate, Float total) {
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

    public CustomerDto getCustomer() {
        return customer;
    }

    public Address getAddress() {
        return address;
    }

    public Card getCard() {
        return card;
    }

    public Collection<ItemDto> getItems() {
        return items;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Float getTotal() {
        return total;
    }
}
