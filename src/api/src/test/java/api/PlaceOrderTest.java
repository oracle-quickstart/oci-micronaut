package api;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.Product;
import api.services.AuthClient;
import api.services.OrdersService;
import api.services.ServiceLocator;
import api.services.UsersClient;
import io.micronaut.core.annotation.Nullable;
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
import io.micronaut.test.support.TestPropertyProvider;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
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
public class PlaceOrderTest implements TestPropertyProvider {
    @Container
    static GenericContainer<?> cartsContainer = new GenericContainer<>(
            DockerImageName.parse("iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/carts:" + getServiceVersion())
    ).withExposedPorts(8080);

    private static String sessionID;
    private final MockAuth mockAuth = new MockAuth();
    private OrdersService.OrderRequest lastOrder;

    private static String getServiceVersion() {
        return "1.0.1-SNAPSHOT";
    }

    @BeforeAll
    static void login(AbstractDatabaseServiceTest.LoginClient client) {
        final HttpResponse<?> response = client.login("test", "password");
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
        Assertions.assertNotNull(sessionID);

        final MuUserDetails userDetails = new MuUserDetails(mockAuth.userId, "testuser");
        final String cardId = UUID.randomUUID().toString();
        final CardInfo cardInfo = new CardInfo(cardId, null, "1245540584", "1009");
        final String addressId = UUID.randomUUID().toString();
        final AddressInfo addressInfo = new AddressInfo(addressId, "10", "Smith st", "SomethingVille", "SomethingCountry", "124345");
        when(usersClient.getProfile(mockAuth.userId))
                .thenReturn(Single.just(userDetails));
        when(usersClient.getCards(mockAuth.userId))
                .thenReturn(Flowable.just(cardInfo));
        when(usersClient.getAddresses(mockAuth.userId))
                .thenReturn(Flowable.just(addressInfo));

        final HttpStatus result = ordersApiClient.placeOrder(sessionID);
        assertEquals(HttpStatus.CREATED, result);
        assertNotNull(lastOrder);
        assertNotNull(lastOrder.getAddress());
        assertNotNull(lastOrder.getCard());
        assertNotNull(lastOrder.getCustomer());
        assertNotNull(lastOrder.getItems());

        assertEquals("http://localhost/" + userDetails.getId() + "/addresses/" + addressId, lastOrder.getAddress());
        assertEquals("http://localhost/" + userDetails.getId() + "/cards/" + cardId, lastOrder.getCard());
    }

    @MockBean(AuthClient.class)
    AuthClient authClient() {
        return mockAuth;
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        return Collections.singletonMap(
                "micronaut.http.services.carts.url", "http://localhost:" + cartsContainer.getFirstMappedPort()
        );
    }

    @MockBean(api.services.CartsService.CatalogueClient.class)
    api.services.CartsService.CatalogueClient catalogueClient() {
        return id -> Maybe.just(new Product(id, 10.00));
    }

    @MockBean(ServiceLocator.class)
    ServiceLocator ServiceLocator() {
        return new ServiceLocator() {
            @Override
            public Single<UriTemplate> getCartsURL() {
                return Single.just(UriTemplate.of("http://localhost"));
            }

            @Override
            public Single<UriTemplate> getUsersURL() {
                return Single.just(UriTemplate.of("http://localhost"));
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
            public Single<Map<String, Object>> getOrders(String custId, @Nullable String sort) {
                return Single.just(Collections.emptyMap());
            }

            @Override
            public Single<Map<String, Object>> getOrder(Long orderId) {
                return Single.just(Collections.emptyMap());
            }

            @Override
            public Single<Map<String, Object>> newOrder(OrdersService.OrderRequest orderRequest) {
                PlaceOrderTest.this.lastOrder = orderRequest;
                return Single.just(Collections.emptyMap());
            }
        };
    }
    @MockBean(UsersClient.class)
    UsersClient usersClient(){
        return mock(UsersClient.class);
    }
}
