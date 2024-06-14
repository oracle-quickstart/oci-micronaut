/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
// import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.micronaut.serde.annotation.Serdeable;

/**
 * The customer business object.
 */
@Entity
@Serdeable
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long customerId;

    @JsonProperty("id")
    private String id;

    private String firstName;
    private String lastName;
    private String username;

    @OneToMany
    private List<Address> addresses = new ArrayList<>();

    @OneToMany
    private List<Card> cards = new ArrayList<>();

    public Customer() {
    }

    public Customer(String id,
                    String firstName,
                    String lastName,
                    String username,
                    List<Address> addresses,
                    List<Card> cards) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.addresses = addresses;
        this.cards = cards;
    }

    public Customer(String firstName,
                    String lastName,
                    String username,
                    List<Address> addresses,
                    List<Card> cards) {
        this(null, firstName, lastName, username, addresses, cards);
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
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the addresses
     */
    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * @param cards the cards to set
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(firstName, customer.firstName) &&
                Objects.equals(lastName, customer.lastName) &&
                Objects.equals(username, customer.username) &&
                Objects.equals(addresses, customer.addresses) &&
                Objects.equals(cards, customer.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, addresses, cards);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", addresses=" + addresses +
                ", cards=" + cards +
                '}';
    }
}
