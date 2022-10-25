package user.controllers.dto;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotEmpty;

/**
 * {@link user.model.UserAddress} DTO.
 */
@Introspected
public class UserAddressDto {

    @NotEmpty
    private final String number;

    @NotEmpty
    private final String street;

    @NotEmpty
    private final String city;

    @NotEmpty
    private final String country;

    @NotEmpty
    private final String postcode;

    public UserAddressDto(String number,
                          String street,
                          String city,
                          String country,
                          String postcode) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public String toString() {
        return "UserAddressDto{" +
                "number='" + number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
