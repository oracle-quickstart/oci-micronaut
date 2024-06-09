package api;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.UserRegistrationRequest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.BasicAuth;
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
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractUsersServiceTest {

    private UserRegistrationRequest userRegistrationRequest;
    private String sessionID;
    @Inject
    UserApiClient client;

    // @NonNull
    // // @Override
    // public Map<String, String> getProperties() {
    //     boolean useMongoDB = false;
    //     boolean useNats = false;
    //     return getProperties(useMongoDB, useNats);
    // }

    @Test
    @Order(1)
    void testShouldFailLogin() {
        HttpClientResponseException error = assertThrows(HttpClientResponseException.class, () ->
                client.login(new BasicAuth("junk", "junk"))
        );
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
    }

    @Test
    @Order(2)
    void testRegister() {
        userRegistrationRequest = new UserRegistrationRequest(
                "fred",
                "testpass",
                "Fred",
                "Flintstone",
                "fred@flinstones.com"
        );
        final Map<String, Object> result = client.register(userRegistrationRequest);
        assertNotNull(result);
        assertNotNull(result.get("attributes"));
        assertTrue(((Map)result.get("attributes")).containsKey("id"));
    }

    @Test
    @Order(3)
    void testLogin() {
        final HttpResponse<?> loginResult = client.login(
                new BasicAuth(userRegistrationRequest.getUsername(), userRegistrationRequest.getPassword()));
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
    void testAddAddress() {
        final AddressInfo original = AddressInfo.createWithoutId("10", "Smith St.", "Fooville", "Foo", "12345");
        AddressInfo addressInfo = client.addAddress(sessionID, original);

        assertNotNull(addressInfo);
        assertEquals("Smith St.", addressInfo.street());
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
    void testAddCard() {
        final CardInfo original = CardInfo.createWithoutId("123", "1234123412341234", "0222");
        CardInfo cardInfo = client.addCard(sessionID, original);
        assertEquals(
                original.expires(),
                cardInfo.expires()
        );

        assertTrue(cardInfo.longNum().startsWith("xxxx"));

        final CardInfo retrieved = client.getCard(sessionID);
        assertEquals(
                cardInfo.expires(),
                retrieved.expires()
        );
        assertTrue(retrieved.longNum().startsWith("xxxx"));
    }

    @Test
    @Order(10)
    void testLogout() {
        client.logout(sessionID);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                assertThrows(HttpClientResponseException.class, () -> client.getProfile(sessionID)).getStatus()
        );
    }

    // // @Override
    // protected String getServiceVersion() {
    //     return "2.0.0-SNAPSHOT";
    // }

    // // @Override
    // protected String getServiceId() {
    //     return "user";
    // }

    @Client("/api")
    interface UserApiClient {
        @Get("/profile")
        Map<String, Object> getProfile(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/login")
        HttpResponse<?> login(BasicAuth basicAuth);

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
