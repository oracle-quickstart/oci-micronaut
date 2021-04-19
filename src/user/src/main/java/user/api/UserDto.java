package user.api;

import io.micronaut.core.annotation.Introspected;
import user.api.dto.UserAddressDto;

import java.util.List;

@Introspected
public class UserDto {

    private final String username;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String phone;

    private final List<UserAddressDto> addresses;

    public UserDto(String username, String password, String firstName, String lastName, String email, String phone, List<UserAddressDto> addresses) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.addresses = addresses;
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

    public String getPhone() {
        return phone;
    }

    public List<UserAddressDto> getAddresses() {
        return addresses;
    }
}
