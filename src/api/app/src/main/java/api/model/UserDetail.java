package api.model;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(title = "User", description = "User details.")
@Serdeable
public class UserDetail {

    @Schema(title = "User uuid.",
            description = "Every user has unique id across the MuShop.",
            example = "a6bd188f-bf9a-4f0f-a9f0-6573dd89419d")
    private final UUID id;

    @Schema(title = "User username.",
            description = "User username used to to log in to MuShop.",
            example = "johndoe")
    private final String username;

    @Schema(title = "User first name.",
            example = "John")
    private final String firstName;

    @Schema(title = "User last name.",
            example = "Doe")
    private final String lastName;

    @Schema(title = "User email.",
            example = "john@doe.com")
    private final String email;

    @Schema(title = "User phone.",
            example = "123 456 789")
    private final String phone;

    @Schema(title = "User account creation date", description = "Date when was the user account created.")
    private final OffsetDateTime createdAt;

    @Schema(title = "User account update data.", description = "Date when was the user account updated.")
    private final OffsetDateTime updatedAt;

    public UserDetail(UUID id, String username, String firstName, String lastName, String email, String phone,
                      OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
