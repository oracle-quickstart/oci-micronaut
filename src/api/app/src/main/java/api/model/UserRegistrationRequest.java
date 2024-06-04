package api.model;

import io.micronaut.core.annotation.Creator;
// import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "User registration", description = "User registration request.")
@Serdeable
public class UserRegistrationRequest {
    @Schema(description = "User username.", example = "johndoe", required = true)
    @NotBlank
    private final String username;

    @Schema(description = "User password.", example = "strongPaswor.d", required = true)
    @NotBlank
    private final String password;

    @Schema(description = "First name.", example = "John", required = true)
    @NotBlank
    private final String firstName;

    @Schema(description = "Last name.", example = "Doe", required = true)
    @NotBlank
    private final String lastName;

    @Schema(description = "User email.", example = "john@doe.com", required = true)
    @Email
    private final String email;

    @Creator
    public UserRegistrationRequest(String username,
                                   String password,
                                   String firstName,
                                   String lastName,
                                   String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
}
