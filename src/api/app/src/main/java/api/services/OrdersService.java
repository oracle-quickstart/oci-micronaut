package api.services;

import api.Application;
import api.model.MuUserDetails;
import api.services.annotation.CartId;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The MuShop orders service forwarder.
 */
@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class OrdersService {

    private static final String USER_ID = "userId";
    private static final String URI_CART_ITEMS = "/carts/{cartId}/items";
    private static final String URI_USER_ID = "/customers/{userId}";
    private static final String URI_USER_ADDRESS = "/customers/{userId}/addresses/{addressId}";
    private static final String URI_USER_CARD = "/customers/{userId}/cards/{cardId}";

    private final OrdersClient client;
    private final UsersClient usersClient;
    private final ServiceLocator serviceLocator;

    public OrdersService(OrdersClient client,
                         UsersClient usersClient,
                         ServiceLocator serviceLocator) {
        this.client = client;
        this.usersClient = usersClient;
        this.serviceLocator = serviceLocator;
    }

    @Operation(
            summary = "List orders",
            description = "List user orders.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns list of user orders."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"orders"}
    )
    @Get("/orders{?sort}")
    Mono<List<?>> getOrders(Authentication authentication,
                            @Nullable @Parameter(description = "Sort orders", example = "createdDate,asc") String sort) {
        final String customerId = MuUserDetails.resolveId(authentication);
        return client.getOrders(customerId, sort)
                .map(stringObjectMap -> {
                    List<?> orders = Collections.EMPTY_LIST;
                    Map<String, Object> m = (Map<String, Object>) stringObjectMap.getOrDefault("_embedded", Collections.EMPTY_MAP);
                    if (m.containsKey("customerOrders")) {
                        Object customerOrders = m.get("customerOrders");
                        if (customerOrders instanceof List) {
                            orders = (List<?>) customerOrders;
                        } else if (customerOrders instanceof Map) {
                            orders = Collections.singletonList(customerOrders);
                        }
                    }
                    return orders;
                });
    }

    @Operation(
            summary = "Get order",
            description = "Get order by order id.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            parameters = {
                    @Parameter(name = "orderId", in = ParameterIn.PATH, required = true, description = "Order id", example = "22")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns order detail."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"orders"}
    )
    @Get("/orders/{orderId}")
    Mono<Map<String, Object>> getOrder(Long orderId) {
        return client.getOrder(orderId);
    }

    @Operation(
            summary = "Create order",
            description = "Creates new order based on specified cartId.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Returns order summary."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"orders"}
    )
    @Post("/orders")
    @Status(HttpStatus.CREATED)
    @TrackEvent("create:order")
    Mono<Map<String, Object>> placeOrder(Authentication authentication, @CartId UUID cartId) {
        final String userId = MuUserDetails.resolveId(authentication);
        return usersClient.getProfile(userId)
                .flatMap(userDetails -> serviceLocator.getUsersURL().flatMap(customerURI ->
                        serviceLocator.getCartsURL().flatMap(cartURI -> {
                            final String customerId = userDetails.getId();
                            return usersClient.getCards(customerId).single().flatMap(cardInfo ->
                                    usersClient.getAddresses(customerId).single().flatMap(addressInfo -> {
                                        OrderRequest orderRequest = new OrderRequest(
                                                cartURI.nest(URI_CART_ITEMS).expand(Map.of("cartId", cartId)),
                                                customerURI.nest(URI_USER_ID).expand(Map.of(USER_ID, userId)),
                                                customerURI.nest(URI_USER_ADDRESS).expand(Map.of(USER_ID, userId, "addressId", addressInfo.id())),
                                                customerURI.nest(URI_USER_CARD).expand(Map.of(USER_ID, userId, "cardId", cardInfo.id()))
                                        );
                                        return client.newOrder(orderRequest);
                                    }));
                        })));
    }

    @Client(id = ServiceLocator.ORDERS, path = "/orders")
    public interface OrdersClient {
        @Get("/search/customer{?custId,sort}")
        Mono<Map<String, Object>> getOrders(String custId, @Nullable String sort);

        @Get("/{orderId}")
        Mono<Map<String, Object>> getOrder(Long orderId);

        @Post
        Mono<Map<String, Object>> newOrder(@Body OrderRequest orderRequest);
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
