package api;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.Product;
import api.services.AuthClient;
import api.services.OrdersService;
import api.services.ServiceLocator;
import api.services.UsersClient;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.uri.UriTemplate;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceOrderTest extends AbstractDatabaseServiceTest {

    private static String sessionID;

    private final MockAuth mockAuth = new MockAuth();
    private OrdersService.OrderRequest lastOrder;

    @Override
    protected String getServiceVersion() {
        return "2.0.0-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "carts";
    }

    @BeforeAll
    static void login(LoginClient client) {
        final HttpResponse<?> response = client.login(new BasicAuth("user", "pass"));
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }

    @Test
    void testPlaceOrder(
            CartsServiceTest.CartClient cartClient,
            UsersClient usersClient,
            OrdersApiClient ordersApiClient) {
        final HttpStatus status = cartClient.addItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 2
        ));

        assertEquals(HttpStatus.CREATED, status);
        assertNotNull(sessionID);

        final MuUserDetails userDetails = new MuUserDetails(mockAuth.userId, "testuser");
        final String cardId = UUID.randomUUID().toString();
        final CardInfo cardInfo = new CardInfo(cardId, null, "1245540584", "1009");
        final String addressId = UUID.randomUUID().toString();
        final AddressInfo addressInfo = new AddressInfo(addressId, "10", "Smith st", "SomethingVille", "SomethingCountry", "124345");
        when(usersClient.getProfile(mockAuth.userId))
                .thenReturn(Mono.just(userDetails));
        when(usersClient.getCards(mockAuth.userId))
                .thenReturn(Flux.just(cardInfo));
        when(usersClient.getAddresses(mockAuth.userId))
                .thenReturn(Flux.just(addressInfo));

        final HttpStatus result = ordersApiClient.placeOrder(sessionID);
        assertEquals(HttpStatus.CREATED, result);
        assertNotNull(lastOrder);
        assertNotNull(lastOrder.getAddress());
        assertNotNull(lastOrder.getCard());
        assertNotNull(lastOrder.getCustomer());
        assertNotNull(lastOrder.getItems());

        assertEquals("http://localhost/customers/" + userDetails.getId() + "/addresses/" + addressId, lastOrder.getAddress());
        assertEquals("http://localhost/customers/" + userDetails.getId() + "/cards/" + cardId, lastOrder.getCard());
    }

    @MockBean(AuthClient.class)
    AuthClient authClient() {
        return mockAuth;
    }

    @NonNull
    @Override
    public Map<String, String> getProperties() {
        boolean useOracleDB = false;
        boolean useMongoDB = true;
        boolean useNats = false;
        return getProperties(useOracleDB, useMongoDB, useNats);
    }

    @MockBean(api.services.CartsService.CatalogueClient.class)
    api.services.CartsService.CatalogueClient catalogueClient() {
        return id -> Mono.just(new Product(id, 10.00));
    }

    @MockBean(ServiceLocator.class)
    ServiceLocator ServiceLocator() {
        return new ServiceLocator() {
            @Override
            public Mono<UriTemplate> getCartsURL() {
                return Mono.just(UriTemplate.of("http://localhost"));
            }

            @Override
            public Mono<UriTemplate> getUsersURL() {
                return Mono.just(UriTemplate.of("http://localhost"));
            }
        };
    }


    @Client("/api")
    interface OrdersApiClient {
        @Post("/orders")
        HttpStatus placeOrder(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);
    }

    @MockBean(OrdersService.OrdersClient.class)
    OrdersService.OrdersClient ordersClient() {
        return new OrdersService.OrdersClient() {

            @Override
            public Mono<Map<String, Object>> getOrders(String custId, @Nullable String sort) {
                return Mono.just(Collections.emptyMap());
            }

            @Override
            public Mono<Map<String, Object>> getOrder(Long orderId) {
                return Mono.just(Collections.emptyMap());
            }

            @Override
            public Mono<Map<String, Object>> newOrder(OrdersService.OrderRequest orderRequest) {
                PlaceOrderTest.this.lastOrder = orderRequest;
                return Mono.just(Collections.emptyMap());
            }
        };
    }

    @MockBean(UsersClient.class)
    UsersClient usersClient() {
        return mock(UsersClient.class);
    }
}
