package api.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import java.util.Objects;

@Schema(title = "User address", description = "User address details.")
@Serdeable
public class AddressInfo {
    @Schema(title = "Address id", example = "22")
    @Nullable
    private final String id;

    @Schema(title = "Street number", example = "2")
    @NotEmpty
    private final String number;

    @Schema(title = "Street name", example = "Round the corner")
    @NotEmpty
    private final String street;

    @Schema(title = "Street name", example = "Lanai")
    @NotEmpty
    private final String city;

    @Schema(title = "Country", example = "Hawaii")
    @NotEmpty
    private final String country;

    @Schema(title = "Postcode", example = "96763")
    @NotEmpty
    private final String postcode;

    @Creator
    public AddressInfo(@Nullable String id,
                       String number,
                       String street,
                       String city,
                       String country,
                       String postcode) {
        this.id = id;
        this.number = number;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    public AddressInfo(String number,
                       String street,
                       String city,
                       String country,
                       String postcode) {
        this(null, number, street, city, country, postcode);
    }

    @Nullable
    public String getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressInfo that = (AddressInfo) o;
        return number.equals(that.number) &&
                street.equals(that.street) &&
                city.equals(that.city) &&
                country.equals(that.country) &&
                postcode.equals(that.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, street, city, country, postcode);
    }
}
