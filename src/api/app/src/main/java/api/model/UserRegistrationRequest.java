package api.model;



import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "User registration", description = "User registration request.")
@Serdeable
public record  UserRegistrationRequest (
        @Schema(description = "User username.", example = "johndoe")
        @NotBlank String username,

        @Schema(description = "User password.", example = "strongPaswor.d")
        @NotBlank String password,

        @Schema(description = "First name.", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name.", example = "Doe")
        @NotBlank String lastName,

        @Schema(description = "User email.", example = "john@doe.com")
        @Email String email){}


