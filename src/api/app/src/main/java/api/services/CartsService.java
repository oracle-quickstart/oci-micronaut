package api.services;

import api.model.MuUserDetails;
import api.model.Product;
import api.services.annotation.CartId;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The MuShop carts service forwarder.
 */
@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class CartsService {

    public static final Logger LOG = LoggerFactory.getLogger(CartsService.class);

    private static final String ITEM_ID = "itemId";
    private static final String UNIT_PRICE = "unitPrice";
    private static final String QUANTITY = "quantity";

    private final CartsClient client;
    private final CatalogueClient catalogueClient;

    CartsService(CartsClient client, CatalogueClient catalogueClient) {
        this.client = client;
        this.catalogueClient = catalogueClient;
    }

    /**
     * Get current cart items.
     *
     * @return Returns items in the cart
     */
    @Tag(name="cart") 
    @Get(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Mono<List<CartItem>> getCart(@Parameter(hidden = true) @CartId UUID cartID) {
        return client.getCartItems(cartID)
                .doOnSuccess(cartItems -> LOG.info("Found cart " + cartItems))
                .onErrorReturn(Collections.emptyList());
    }

    /**
     * Deletes current cart
     */
    @Tag(name="cart") 
    @ApiResponse(responseCode = "204", description = "Cart deleted.")
    @Delete(value = "/cart", produces = MediaType.APPLICATION_JSON)
    @Status(HttpStatus.NO_CONTENT)
    @TrackEvent("delete:cart")
    Mono<Void> deleteCart(@Parameter(hidden = true) @CartId UUID cartID) {
        return client.deleteCart(cartID)
                    .onErrorResume(throwable -> Mono.empty());
    }

    /**
     * Adds item to current cart.
     */
    @ApiResponse(responseCode = "201", description = "Item added to cart.")
    @Tag(name="cart") 
    @Status(HttpStatus.CREATED)
    @Post(value = "/cart")
    @TrackEvent("cart:addItem")
    Mono<Void>  addItem(
            @Nullable Authentication authentication,
            @Parameter(hidden = true) @CartId UUID cartId,
            @Body ItemUpdate addItem) {
        return catalogueClient.getItem(addItem.getId())
            .switchIfEmpty(Mono.error(() ->
                new HttpStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + addItem.getId())
            )).flatMap((product ->
                    client.postCart(cartId, Map.of(
                           "customerId", authentication == null ? "" : MuUserDetails.resolveId(authentication),
                           "items", Collections.singletonList(Map.of(
                                    ITEM_ID, product.getId(),
                                    UNIT_PRICE, product.getPrice(),
                                    QUANTITY, addItem.quantity
                            ))
                    )).flatMap(httpStatus -> {
                        if (httpStatus.getCode() > 201) {
                            return Mono.error(new HttpStatusException(httpStatus, "Unable to add to cart"));
                        }
                        return Mono.empty();
                    })
            ));
    }

    /**
     * Updates item in current cart.
     *
     */
    @Tag(name="cart") 
    @ApiResponse(responseCode = "200", description = "Item updated.")
    @Status(HttpStatus.OK)
    @Post(value = "/cart/update")
    Mono<Void>  updateItem(
            @Parameter(hidden = true) @CartId UUID cartId,
            @Body ItemUpdate addItem) {
        return catalogueClient.getItem(addItem.id)
                .switchIfEmpty(Mono.error(() ->
                        new HttpStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + addItem.id)
                )).flatMap((product ->
                        client.updateCartItem(cartId, Map.of(
                                ITEM_ID, product.getId(),
                                UNIT_PRICE, product.getPrice(),
                                QUANTITY, addItem.getQuantity()
                        )).flatMap(httpStatus -> {
                            if (httpStatus.getCode() > 201) {
                                return Mono.error(new HttpStatusException(httpStatus, "Unable to update to cart"));
                            }
                            return Mono.empty();
                        })
                ));
    }

    /**
     * Deletes item from cart.
     *
     * @param id The item ID
     */
    @ApiResponse(responseCode = "204", description = "Item removed.")
    @Tag(name="cart") 
    @Delete(value = "/cart/{id}", produces = MediaType.APPLICATION_JSON)
    @Status(HttpStatus.NO_CONTENT)
    @TrackEvent("delete:cartItem")
    Mono<Void>  deleteCartItem( @Parameter(hidden = true) @CartId UUID cartID, String id) {
        return client.deleteCartItem(cartID, id);
    }

    @Client(id = ServiceLocator.CATALOGUE, path = "/catalogue")
    public interface CatalogueClient {
        @Get("/{id}")
        Mono<Product> getItem(String id);
    }

    @Client(id = ServiceLocator.CARTS, path = "/carts")
    interface CartsClient {
        @Get(uri = "/{cartId}/items", produces = MediaType.APPLICATION_JSON)
        Mono<List<CartItem>> getCartItems(UUID cartId);

        @Delete(uri = "/{cartId}")
        Mono<Void>  deleteCart(UUID cartId);

        @Delete(uri = "/{cartId}/items/{itemId}", produces = MediaType.APPLICATION_JSON)
        Mono<Void>  deleteCartItem(UUID cartId, String itemId);

        @Post(uri = "/{cartId}", processes = MediaType.APPLICATION_JSON)
        Mono<HttpStatus> postCart(UUID cartId, @Body Map<String, Object> body);

        @Put(uri = "/{cartId}/items", processes = MediaType.APPLICATION_JSON)
        Mono<HttpStatus> updateCartItem(UUID cartId, @Body Map<String, Object> body);
    }

    @Schema(title = "Cart item update")
    @Introspected
    static class ItemUpdate {
        @NotBlank
        private final String id;
        @Min(1)
        private final int quantity;

        ItemUpdate(String id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        /**
         * Item id.
         */
        public String getId() {
            return id;
        }

        /**
         * New quantity of item.
         */
        public int getQuantity() {
            return quantity;
        }
    }

    @Schema(title = "Cart item")
    @Introspected
    static class CartItem {

        private final String id;

        private final String itemId;

        private final int quantity;

        private final BigDecimal unitPrice;

        public CartItem() {
            this(UUID.randomUUID().toString(), null, 1, null);
        }

        @Creator
        public CartItem(String id, String itemId, int quantity, BigDecimal unitPrice) {
            this.id = id;
            this.itemId = itemId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        /**
         * Item id.
         */
        public String getId() {
            return id;
        }

        /**
         * Item name.
         */
        public String getItemId() {
            return itemId;
        }

        /**
         * Item quantity.
         */
        public int getQuantity() {
            return quantity;
        }

        /**
         * Item unit price.
         */
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
