/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long addressId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("number")
    private String street_number;

    private String street;
    private String city;
    private String postcode;
    private String country;

    public Address(String id,
                   String street_number,
                   String street,
                   String city,
                   String postcode,
                   String country) {
        this.id = id;
        this.street_number = street_number;
        this.street = street;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    public Address() {
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
     * @return the addressId
     */
    public Long getAddressId() {
        return addressId;
    }

    /**
     * @param addressId the addressId to set
     */
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the street_number
     */
    public String getStreet_number() {
        return street_number;
    }

    /**
     * @param street_number the street_number to set
     */
    public void setStreet_number(String street_number) {
        this.street_number = street_number;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode the postcode to set
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(id, address.id) &&
                Objects.equals(street_number, address.street_number) &&
                Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(postcode, address.postcode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street_number, street, city, postcode, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", id='" + id + '\'' +
                ", street_number='" + street_number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
