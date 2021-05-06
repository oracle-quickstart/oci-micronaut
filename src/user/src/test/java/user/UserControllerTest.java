package user;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import user.api.LoginOperations;
import user.api.dto.UserDto;
import user.api.UserOperations;
import user.api.dto.UserAddressDetailDto;
import user.api.dto.UserAddressDto;
import user.api.dto.UserCardDetailDto;
import user.api.dto.UserCardDto;
import user.api.dto.UserDetailDto;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@MicronautTest
public class UserControllerTest {
    @Inject
    UserClient client;

    @Test
    void testCreate() {
        UserDetailDto userDetail = createUser();

        assertNotNull(userDetail.getId());
        assertNotNull(userDetail.getCreatedAt());
        assertNotNull(userDetail.getUpdatedAt());
        assertEquals("bunny", userDetail.getUsername());
        assertEquals("Bugs", userDetail.getFirstName());
        assertEquals("Bunny", userDetail.getLastName());
        assertEquals("bunny@bugs.nnn", userDetail.getEmail());
        assertEquals("12345", userDetail.getPhone());

        userDetail = client.getUser(userDetail.getId());
        assertEquals(2, userDetail.getAddresses().size());

        UserAddressDetailDto address1 = userDetail.getAddresses().stream().filter(a -> a.getNumber().equals("123")).findFirst().orElseThrow();
        UserAddressDetailDto address2 = userDetail.getAddresses().stream().filter(a -> a.getNumber().equals("345")).findFirst().orElseThrow();

        assertNotNull(address1.getId());
        assertNotNull(address1.getCreatedAt());
        assertNotNull(address1.getUpdatedAt());
        assertEquals("123", address1.getNumber());
        assertEquals("Street XYZ", address1.getStreet());
        assertEquals("CityA", address1.getCity());
        assertEquals("CountryB", address1.getCountry());
        assertEquals("11222", address1.getPostcode());

        assertNotNull(address2.getId());
        assertNotNull(address2.getCreatedAt());
        assertNotNull(address2.getUpdatedAt());
        assertEquals("345", address2.getNumber());
        assertEquals("Street ABC", address2.getStreet());
        assertEquals("CityB", address2.getCity());
        assertEquals("CountryG", address2.getCountry());
        assertEquals("66555", address2.getPostcode());

        client.deleteUser(userDetail.getId());
    }

    @Test
    void testUpdate() {
        UserDetailDto userDetail = createUser();

        UserDto newUserDetailDto = new UserDto("bunny2",
                "spacejam2",
                "Bugs2",
                "Bunny2",
                "bunny2@bugs.nnn",
                "12345222", null);

        client.updateUser(userDetail.getId(), newUserDetailDto);
        userDetail = client.getUser(userDetail.getId());

        assertNotNull(userDetail.getId());
        assertNotNull(userDetail.getCreatedAt());
        assertNotNull(userDetail.getUpdatedAt());
        assertEquals("bunny2", userDetail.getUsername());
        assertEquals("Bugs2", userDetail.getFirstName());
        assertEquals("Bunny2", userDetail.getLastName());
        assertEquals("bunny2@bugs.nnn", userDetail.getEmail());
        assertEquals("12345222", userDetail.getPhone());
        assertEquals(2, userDetail.getAddresses().size());

        client.deleteUser(userDetail.getId());
    }

    @Test
    void testAddAndDeleteAddress() {
        UserDetailDto userDetail = createUser();

        UserAddressDetailDto address = client.addUserAddress(userDetail.getId(),
                new UserAddressDto("333", "Street 3", "City3", "Country3", "333333"));

        assertNotNull(address.getId());
        assertNotNull(address.getCreatedAt());
        assertNotNull(address.getUpdatedAt());
        assertEquals("333", address.getNumber());
        assertEquals("Street 3", address.getStreet());
        assertEquals("City3", address.getCity());
        assertEquals("Country3", address.getCountry());
        assertEquals("333333", address.getPostcode());

        userDetail = client.getUser(userDetail.getId());

        assertEquals(3, userDetail.getAddresses().size());

        address = userDetail.getAddresses().stream().filter(a -> a.getNumber().equals("333")).findFirst().orElseThrow();

        assertNotNull(address.getId());
        assertNotNull(address.getCreatedAt());
        assertNotNull(address.getUpdatedAt());
        assertEquals("333", address.getNumber());
        assertEquals("Street 3", address.getStreet());
        assertEquals("City3", address.getCity());
        assertEquals("Country3", address.getCountry());
        assertEquals("333333", address.getPostcode());

        List<UserAddressDetailDto> userAddresses = client.getUserAddresses(userDetail.getId());
        assertEquals(3, userAddresses.size());

        address = client.getUserAddress(userDetail.getId(), userAddresses.get(0).getId());
        assertEquals(address.getId(), userAddresses.get(0).getId());

        client.deleteUserAddress(userDetail.getId(), address.getId());

        userAddresses = client.getUserAddresses(userDetail.getId());
        assertEquals(2, userAddresses.size());

        client.deleteUser(userDetail.getId());
    }

    @Test
    void testAddAndDeleteCard() {
        UserDetailDto userDetail = createUser();

        UserCardDetailDto userCard = client.addUserCard(userDetail.getId(),
                new UserCardDto("123", "0123456789123456", "0310"));

        assertNotNull(userCard.getId());
        assertNotNull(userCard.getCreatedAt());
        assertNotNull(userCard.getUpdatedAt());
        assertEquals("3456", userCard.getNumber());
        assertEquals("xxxxxxxxxxxx3456", userCard.getLongNum());
        assertEquals("0310", userCard.getExpires());

        userDetail = client.getUser(userDetail.getId());

        assertEquals(1, userDetail.getCards().size());
        userCard = userDetail.getCards().stream().filter(a -> a.getNumber().equals("3456")).findFirst().orElseThrow();

        assertNotNull(userCard.getId());
        assertNotNull(userCard.getCreatedAt());
        assertNotNull(userCard.getUpdatedAt());
        assertEquals("3456", userCard.getNumber());
        assertEquals("xxxxxxxxxxxx3456", userCard.getLongNum());
        assertEquals("0310", userCard.getExpires());

        List<UserCardDetailDto> cards = client.getUserCards(userDetail.getId());
        assertEquals(1, cards.size());
        userCard = client.getUserCard(userDetail.getId(), cards.get(0).getId());
        assertEquals(userCard.getId(), cards.get(0).getId());

        client.deleteUserCard(userDetail.getId(), userCard.getId());

        cards = client.getUserCards(userDetail.getId());
        assertEquals(0, cards.size());

        client.deleteUser(userDetail.getId());
    }

    @Test
    void testLogin() {
        UserDetailDto userDetail = createUser();

        UserDetailDto detailDto = client.login("bunny", "spacejam");
        assertNotNull(detailDto);

        try {
            client.login("bunny", "badpassword");
            fail();
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("User with username: bunny doesn't match provided password", e.getMessage());
        }

        try {
            client.login("unknownusername", "spacejam");
            fail();
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("User with username: unknownusername not found", e.getMessage());
        }

        client.deleteUser(userDetail.getId());
    }

    private UserDetailDto createUser() {
        UserDto userDetailDto = new UserDto("bunny",
                "spacejam",
                "Bugs",
                "Bunny",
                "bunny@bugs.nnn",
                "12345", List.of(
                new UserAddressDto("123", "Street XYZ", "CityA", "CountryB", "11222"),
                new UserAddressDto("345", "Street ABC", "CityB", "CountryG", "66555")
        ));

        return client.createUser(userDetailDto);
    }

    @Client("/")
    interface UserClient extends UserOperations, LoginOperations {
    }
}
