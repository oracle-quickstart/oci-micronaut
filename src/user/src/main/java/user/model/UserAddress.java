package user.model;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;

import java.time.OffsetDateTime;
import java.util.UUID;

@MappedEntity("USER_ADDRESSES")
public class UserAddress {

    @Id
    @AutoPopulated(updateable = false)
    private UUID id;

    @MappedProperty("nmbr")
    private String number;

    private String street;

    private String city;

    private String country;

    private String postcode;

    @DateCreated
    private OffsetDateTime createdAt;

    @DateUpdated
    private OffsetDateTime updatedAt;

    @Relation(value = Relation.Kind.MANY_TO_ONE)
    private User user;

    public UserAddress() {
    }

    public UserAddress(String number, String street, String city, String country, String postcode) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
