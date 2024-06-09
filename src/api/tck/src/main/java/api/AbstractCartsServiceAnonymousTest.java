package api;

import api.model.AssetsLocation;
import api.model.Product;
import api.services.AssetsClient;
import api.services.AuthClient;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractCartsServiceAnonymousTest {

    private static String sessionID;
    @Inject
    private static ConfigClient configClient;
    @Inject
    private static CartClient client;


    @BeforeAll
    static void anonymousCartId() {
        final HttpResponse<?> response = configClient.getConfig();
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }

    @Test
    @Order(1)
    void testReadNonExistentCart() {
        final List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    @Order(2)
    void testDeleteNonExistentCart() {
        final HttpStatus status = client.deleteCart(sessionID);
        assertNotNull(status);
        assertEquals(HttpStatus.NO_CONTENT, status);
    }

    @Test
    @Order(3)
    void testAddItemToCart() {
        final HttpStatus status = client.addItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 2
        ));

        assertEquals(HttpStatus.CREATED, status);

        final List<ProductAndQuantity> cart = client.getCart(sessionID);

        assertEquals(1, cart.size());

        final ProductAndQuantity product = cart.iterator().next();
        assertEquals(10.00, product.price());
        assertEquals(2, product.quantity);

    }

    @Test
    @Order(4)
    void testUpdateItem() {
        List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertEquals(1, cart.size());
        client.updateItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 3
        ));

        cart = client.getCart(sessionID);
        assertEquals(1, cart.size());

        final ProductAndQuantity product = cart.iterator().next();
        assertEquals(10.00, product.price());
        assertEquals(3, product.quantity);
    }

    @Test
    @Order(5)
    void testDeleteItemFromCart() {
        List<ProductAndQuantity> cart = client.getCart(sessionID);
        assertEquals(1, cart.size());
        final HttpStatus status = client.deleteCartItem(sessionID, "1234");
        assertEquals(HttpStatus.NO_CONTENT, status);

        cart = client.getCart(sessionID);
        assertEquals(0, cart.size());
    }

    @Client("/api/config")
    interface ConfigClient {
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
        return id -> Mono.just(new Product(id, 10.00));
    }

    @MockBean(AssetsClient.class)
    AssetsClient assetsClient() {
        return () -> Mono.just(new AssetsLocation("/test"));
    }

    @Serdeable
//    static class ProductAndQuantity extends Product {
//        public final int quantity;
//
//        @JsonCreator
//        public ProductAndQuantity(@JsonProperty("id") String id,
//                                  @JsonProperty("unitPrice") double price,
//                                  @JsonProperty("quantity") int quantity) {
//            super(id, price);
//            this.quantity = quantity;
//        }
//    }
    public record ProductAndQuantity(Product product, int quantity) {

        @JsonCreator
        public ProductAndQuantity(@JsonProperty("id") String id,
                                  @JsonProperty("unitPrice") double price,
                                  @JsonProperty("quantity") int quantity) {
            this(new Product(id, price), quantity);
        }

        @JsonProperty("id")
        public String id() {
            return product.id();
        }

        @JsonProperty("unitPrice")
        public Double price() {
            return product.price();
        }
    }
}
