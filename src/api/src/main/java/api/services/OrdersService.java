package api.services;

import api.model.MuUserDetails;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import api.services.annotation.CartId;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Single;

import java.util.Map;
import java.util.UUID;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class OrdersService {
    private static final String USER_ID = "userId";
    private static final String URI_CART_ITEMS = "/{cartId}/items";
    private static final String URI_USER_ID = "/{userId}";
    private static final String URI_USER_ADDRESS = "/{userId}/addresses/{addressId}";
    private static final String URI_USER_CARD = "/{userId}/cards/{cardId}";
    private final OrdersClient client;
    private final UsersClient usersClient;
    private final ServiceLocator serviceLocator;

    public OrdersService(OrdersClient client, UsersClient usersClient, ServiceLocator serviceLocator) {
        this.client = client;
        this.usersClient = usersClient;
        this.serviceLocator = serviceLocator;
    }

    @Get("/orders{?sort}")
    Single<Map<String, Object>> getOrders(Authentication authentication, @Nullable String sort) {
        final String customerId = MuUserDetails.resolveId(authentication);
        return client.getOrders(customerId, sort);
    }

    @Get("/orders/{orderId}")
    Single<Map<String, Object>> getOrder(Long orderId) {
        return client.getOrder(orderId);
    }

    @Post("/orders")
    @Status(HttpStatus.CREATED)
    @TrackEvent("create:order")
    Single<Map<String, Object>> placeOrder(Authentication authentication, @CartId UUID cartId) {
        final String userId = MuUserDetails.resolveId(authentication);
        return usersClient.getProfile(userId)
                .flatMap(userDetails -> serviceLocator.getUsersURL().flatMap(customerURI ->
                        serviceLocator.getCartsURL().flatMap(cartURI -> {
                    final String customerId = userDetails.getId();
                    return usersClient.getCards(customerId).firstOrError().flatMap(cardInfo ->
                            usersClient.getAddresses(customerId).firstOrError().flatMap(addressInfo -> {
                                OrderRequest orderRequest = new OrderRequest(
                                        cartURI.nest(URI_CART_ITEMS).expand(Map.of("cartId", cartId)),
                                        customerURI.nest(URI_USER_ID).expand(Map.of(USER_ID, userId)),
                                        customerURI.nest(URI_USER_ADDRESS).expand(Map.of(USER_ID, userId, "addressId", addressInfo.getId())),
                                        customerURI.nest(URI_USER_CARD).expand(Map.of(USER_ID, userId, "cardId", cardInfo.getId()))
                                );
                                return client.newOrder(orderRequest);
                            }));
                })));
    }

    @Client(id = ServiceLocator.ORDERS, path = "/orders")
    public interface OrdersClient {
        @Get("/search/customer{?custId,sort}")
        Single<Map<String, Object>> getOrders(String custId, @Nullable String sort);

        @Get("/{orderId}")
        Single<Map<String, Object>> getOrder(Long orderId);

        @Post("orders")
        Single<Map<String, Object>> newOrder(@Body OrderRequest orderRequest);
    }

    @Introspected
    public static class OrderRequest {
        private final String items;
        private final String customer;
        private final String address;
        private final String card;

        OrderRequest(String items, String customer, String address, String card) {
            this.items = items;
            this.customer = customer;
            this.address = address;
            this.card = card;
        }

        public String getItems() {
            return items;
        }

        public String getCustomer() {
            return customer;
        }

        public String getAddress() {
            return address;
        }

        public String getCard() {
            return card;
        }
    }
}
