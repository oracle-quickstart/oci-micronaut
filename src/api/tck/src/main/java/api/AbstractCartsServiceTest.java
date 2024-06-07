package api;

import api.model.Product;
import api.services.AuthClient;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
// import io.micronaut.http.client.annotation.LoginClient;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// @Testcontainers
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractCartsServiceTest {

    private static String sessionID;

    // @BeforeAll
    // static void login(LoginClient client) {
    //     final HttpResponse<?> response = client.login(new BasicAuth("user", "pass"));
    //     final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
    //     sessionID = session.getValue();
    // }

    // @NonNull
    // @Override
    // public Map<String, String> getProperties() {
    //     boolean useMongoDB = true;
    //     boolean useNats = false;
    //     return getProperties(useMongoDB, useNats);
    // }

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
        assertEquals(10.00, product.getPrice());
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
        assertEquals(10.00, product.getPrice());
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
        return id -> Mono.just(new Product(id, 10.00));
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

    // @Override
    // protected String getServiceId() {
    //     return "carts";
    // }

    // @Override
    // protected String getServiceVersion() {
    //     return "2.0.0-SNAPSHOT";
    // }
}
