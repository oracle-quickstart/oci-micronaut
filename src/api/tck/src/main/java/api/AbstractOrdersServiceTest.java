package api;

import api.services.AuthClient;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// @Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractOrdersServiceTest {

    private static String sessionID;

    // @NonNull
    // @Override
    // public Map<String, String> getProperties() {
    //     boolean useMongoDB = false;
    //     boolean useNats = true;
    //     return getProperties(useMongoDB, useNats);
    // }

    // @BeforeAll
    // static void login(LoginClient client) {
    //     final HttpResponse<?> response = client.login(new BasicAuth("user", "pass"));
    //     final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
    //     sessionID = session.getValue();
    // }

    @Test
    void testListOrders(OrdersApiClient client) {
        final List<?> orders = client.getOrders(sessionID);
        assertEquals(0, orders.size());
    }

    // @Override
    // protected String getServiceVersion() {
    //     return "2.0.0-SNAPSHOT";
    // }

    // @Override
    // protected String getServiceId() {
    //     return "orders";
    // }

    // @Override
    // protected int getServiceExposedPort() {
    //     return 8082;
    // }

    @Client("/api")
    interface OrdersApiClient {
        @Get("/orders")
        List<Object> getOrders(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);
    }

    @MockBean(AuthClient.class)
    AuthClient authClient() {
        return new MockAuth();
    }
}
