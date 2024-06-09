package api.model;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(title = "User", description = "User details.")
@Serdeable
public record UserDetail(
        @Schema(title = "User uuid.", description = "Every user has unique id across the MuShop.", example = "a6bd188f-bf9a-4f0f-a9f0-6573dd89419d")
        UUID id,

        @Schema(title = "User username.", description = "User username used to to log in to MuShop.", example = "johndoe")
        String username,

        @Schema(title = "User first name.", example = "John")
        String firstName,

        @Schema(title = "User last name.", example = "Doe")
        String lastName,

        @Schema(title = "User email.", example = "john@doe.com")
        String email,

        @Schema(title = "User phone.", example = "123 456 789")
        String phone,

        @Schema(title = "User account creation date", description = "Date when was the user account created.")
        OffsetDateTime createdAt,

        @Schema(title = "User account update data.", description = "Date when was the user account updated.")
        OffsetDateTime updatedAt){}



