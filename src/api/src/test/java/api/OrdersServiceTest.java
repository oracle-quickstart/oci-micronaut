package api;

import api.services.AuthClient;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdersServiceTest extends AbstractDatabaseServiceTest {

    private static String sessionID;

    @Container
    static GenericContainer<?> natsContainer =
            new GenericContainer<>("nats:latest")
                    .withExposedPorts(4222)
                    .withNetwork(Network.SHARED)
                    .withNetworkAliases("nats-local")
                    .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server is ready.*"));

    @Override
    protected GenericContainer<?> initService() {
        return super.initService()
                    .withEnv("NATS_ADDRESSES", "nats://nats-local:4222");
    }


    @BeforeAll
    static void login(LoginClient client) {
        final HttpResponse<?> response = client.login(new BasicAuth("user", "pass"));
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }

    @Test
    void testListOrders(OrdersApiClient client) {
        final List<?> orders = client.getOrders(sessionID);
        assertEquals(0, orders.size());
    }

    @Override
    protected String getServiceVersion() {
        return "2.0.0-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "orders";
    }

    @Override
    protected int getServiceExposedPort() {
        return 8082;
    }

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
