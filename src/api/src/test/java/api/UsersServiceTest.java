package api;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.UserRegistrationRequest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersServiceTest extends AbstractDatabaseServiceTest {

    private UserRegistrationRequest userRegistrationRequest;
    private String sessionID;

    @Test
    @Order(1)
    void testShouldFailLogin(UserApiClient client) {
        HttpClientResponseException error = assertThrows(HttpClientResponseException.class, () ->
                client.login("junk", "junk")
        );
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
    }

    @Test
    @Order(2)
    void testRegister(UserApiClient client) {
        userRegistrationRequest = new UserRegistrationRequest(
                "fred",
                "testpass",
                "Fred",
                "Flintstone",
                "fred@flinstones.com"
        );
        final Map<String, Object> result = client.register(userRegistrationRequest);
        assertNotNull(result);
        assertEquals(true, result.get("authenticated"));
    }

    @Test
    @Order(3)
    void testLogin(UserApiClient client) {
        final HttpResponse<?> loginResult = client.login(userRegistrationRequest.getUsername(), userRegistrationRequest.getPassword());
        assertEquals(HttpStatus.SEE_OTHER, loginResult.getStatus());
        assertTrue(loginResult.getHeaders().contains(HttpHeaders.AUTHORIZATION_INFO));
        assertTrue(loginResult.getHeaders().contains(HttpHeaders.SET_COOKIE));

        final Cookie session = loginResult.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
        final Map<String, Object> profile = client.getProfile(sessionID);

        assertNotNull(profile);
    }

    @Order(4)
    @Test
    void testAddAddress(UserApiClient client) {
        final AddressInfo original = new AddressInfo("10", "Smith St.", "Fooville", "Foo", "12345");
        AddressInfo addressInfo = client.addAddress(sessionID, original);

        assertNotNull(addressInfo);
        assertEquals("Smith St.", addressInfo.getStreet());
        assertEquals(
                original,
                addressInfo
        );

        assertEquals(
                addressInfo,
                client.getAddress(sessionID)
        );

    }

    @Order(5)
    @Test
    void testAddCard(UserApiClient client) {
        final CardInfo original = new CardInfo("123", "1234123412341234", "0222");
        CardInfo cardInfo = client.addCard(sessionID, original);
        assertEquals(
                original.getExpires(),
                cardInfo.getExpires()
        );

        assertTrue(cardInfo.getLongNum().startsWith("xxxx"));

        final CardInfo retrieved = client.getCard(sessionID);
        assertEquals(
                cardInfo.getExpires(),
                retrieved.getExpires()
        );
        assertTrue(retrieved.getLongNum().startsWith("xxxx"));
    }

    @Test
    @Order(10)
    void testLogout(UserApiClient client) {
        client.logout(sessionID);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                assertThrows(HttpClientResponseException.class, () -> client.getProfile(sessionID)).getStatus()
        );
    }

    @Override
    protected String getServiceVersion() {
        return "1.0.0-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "user";
    }

    @Client("/api")
    interface UserApiClient {
        @Get("/profile")
        Map<String, Object> getProfile(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/login")
        HttpResponse<?> login(String username, String password);

        @Post("/address")
        AddressInfo addAddress(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID, @Body AddressInfo addressInfo);

        @Get("/address")
        AddressInfo getAddress(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/card")
        CardInfo addCard(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID, @Body CardInfo addressInfo);

        @Get("/card")
        CardInfo getCard(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Get("/logout")
        HttpResponse<?> logout(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/register")
        Map<String, Object> register(@Body UserRegistrationRequest request);
    }
}
