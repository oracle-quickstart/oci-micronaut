package user.api.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link user.model.UserAddress} detailed DTO.
 */
@Serdeable
public class UserAddressDetailDto {

    private final UUID id;
    private final String number;
    private final String street;
    private final String city;
    private final String country;
    private final String postcode;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
    private final UserDetailDto user;

    public UserAddressDetailDto(UUID id,
                                String number,
                                String street,
                                String city,
                                String country,
                                String postcode,
                                OffsetDateTime createdAt,
                                OffsetDateTime updatedAt,
                                @Nullable UserDetailDto user) {
        this.id = id;
        this.number = number;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public UUID getId() {
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserDetailDto getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserAddressDetailDto{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                '}';
    }
}
