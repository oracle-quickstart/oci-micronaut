package mushop.orders.controllers.dto;

import io.micronaut.core.annotation.Introspected;

/**
 * The {@link mushop.orders.entities.Customer} DTO.
 */
@Introspected
public class CustomerDto {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final String username;

    public CustomerDto(String id,
                       String firstName,
                       String lastName,
                       String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }
}
