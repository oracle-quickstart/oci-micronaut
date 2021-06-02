package api;

import api.model.Product;
import api.services.AuthClient;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Maybe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartsServiceAnonymousTest extends AbstractDatabaseServiceTest {

    private static String sessionID;

    @BeforeAll
    static void anonymousCartId(ConfigClient client) {
        final HttpResponse<?> response = client.getConfig();
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }

    @Test
    @Order(1)
    void testReadNonExistentCart(CartClient client) {
        final List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertNotNull(cart);
    }

    @Test
    @Order(2)
    void testDeleteNonExistentCart(CartClient client) {
        final HttpStatus status = client.deleteCart(sessionID);
        assertNotNull(status);
        assertEquals(HttpStatus.NO_CONTENT, status);
    }

    @Test
    @Order(3)
    void testAddItemToCart(CartClient client) {
        final HttpStatus status = client.addItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 2
        ));

        assertEquals(HttpStatus.CREATED, status);

        final List<ProductAndQuantity> cart = client.getCart(sessionID);

        assertEquals(1, cart.size());

        final ProductAndQuantity product = cart.iterator().next();
        assertEquals(10.00, product.getUnitPrice());
        assertEquals(2, product.quantity);

    }

    @Test
    @Order(4)
    void testUpdateItem(CartClient client) {
        List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertEquals(1, cart.size());
        client.updateItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 3
        ));

        cart = client.getCart(sessionID);
        assertEquals(1, cart.size());

        final ProductAndQuantity product = cart.iterator().next();
        assertEquals(10.00, product.getUnitPrice());
        assertEquals(3, product.quantity);
    }

    @Test
    @Order(5)
    void testDeleteItemFromCart(CartClient client) {
        List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertEquals(1, cart.size());
        final HttpStatus status = client.deleteCartItem(sessionID, "1234");
        assertEquals(HttpStatus.NO_CONTENT, status);

        cart = client.getCart(sessionID);
        assertEquals(0, cart.size());
    }

    @Client("/api/config")
    interface ConfigClient{
        @Get
        HttpResponse<?> getConfig();
    }

    @Client("/api/cart")
    interface CartClient {
        @Get
        List<ProductAndQuantity> getCart(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Delete
        HttpStatus deleteCart(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Delete("/{itemId}")
        HttpStatus deleteCartItem(
                @CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID,
                String itemId
        );

        @Post
        HttpStatus addItem(
                @CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID,
                @Body Map<String, Integer> item
        );

        @Post("/update")
        HttpStatus updateItem(
                @CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID,
                @Body Map<String, Integer> item
        );
    }

    @MockBean(AuthClient.class)
    AuthClient authClient() {
        return new MockAuth();
    }

    @MockBean(api.services.CartsService.CatalogueClient.class)
    api.services.CartsService.CatalogueClient catalogueClient() {
        return id -> Maybe.just(new Product(id, 10.00));
    }

    @Introspected
    static class ProductAndQuantity extends Product {
        public final int quantity;

        @JsonCreator
        public ProductAndQuantity(@JsonProperty("id") String id,
                                  @JsonProperty("unitPrice") double price,
                                  @JsonProperty("quantity") int quantity) {
            super(id, price);
            this.quantity = quantity;
        }
    }

    @Override
    protected GenericContainer<?> initService() {
        return new GenericContainer<>(composeServiceDockerImage()).withExposedPorts(getServiceExposedPort())
                .withNetwork(Network.SHARED)
                .withEnv(Map.of(
                        "DATASOURCES_DEFAULT_URL", "jdbc:oracle:thin:system/oracle@oracledb:1521:xe",
                        "DATASOURCES_DEFAULT_USERNAME", oracleContainer.getUsername(),
                        "DATASOURCES_DEFAULT_PASSWORD", oracleContainer.getPassword(),
                        "DATASOURCES_DEFAULT_DRIVER_CLASS_NAME", "oracle.jdbc.OracleDriver",
                        "SODA_CREATE_USERNAME", "true"
                ));
    }

    @Override
    protected String getServiceId() {
        return "carts";
    }

    @Override
    protected String getServiceVersion() {
        return "1.0.3-SNAPSHOT";
    }
}
