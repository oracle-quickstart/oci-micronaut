package user.api.dto;

import io.micronaut.core.annotation.Introspected;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Introspected
public class UserDetailDto {

    private final UUID id;

    private final String username;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String phone;

    private final List<UserAddressDetailDto> addresses;

    private final List<UserCardDetailDto> cards;

    private final OffsetDateTime createdAt;

    private final OffsetDateTime updatedAt;

    public UserDetailDto(UUID id, String username, String firstName, String lastName, String email, String phone,
                         List<UserAddressDetailDto> addresses, List<UserCardDetailDto> cards,
                         OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.addresses = addresses;
        this.cards = cards;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<UserAddressDetailDto> getAddresses() {
        return addresses;
    }

    public List<UserCardDetailDto> getCards() {
        return cards;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "UserDetailDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", addresses=" + addresses +
                ", cards=" + cards +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
