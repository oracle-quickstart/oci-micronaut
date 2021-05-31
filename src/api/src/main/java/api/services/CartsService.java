package api.services;

import api.model.MuUserDetails;
import api.model.Product;
import api.services.annotation.CartId;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
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
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class CartsService {

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
    Single<List<CartItem>> getCart(@Parameter(hidden = true) @CartId UUID cartID) {
        return client.getCartItems(cartID)
                .onErrorReturnItem(Collections.emptyList());
    }

    /**
     * Deletes current cart
     */
    @Tag(name="cart") 
    @ApiResponse(responseCode = "204", description = "Cart deleted.")
    @Delete(value = "/cart", produces = MediaType.APPLICATION_JSON)
    @Status(HttpStatus.NO_CONTENT)
    @TrackEvent("delete:cart")
    Completable deleteCart(@Parameter(hidden = true) @CartId UUID cartID) {
        return client.deleteCart(cartID)
                    .onErrorComplete();
    }

    /**
     * Adds item to current cart.
     */
    @ApiResponse(responseCode = "201", description = "Item added to cart.")
    @Tag(name="cart") 
    @Status(HttpStatus.CREATED)
    @Post(value = "/cart")
    @TrackEvent("cart:addItem")
    Completable addItem(
            @Nullable Authentication authentication,
            @Parameter(hidden = true) @CartId UUID cartId,
            @Body ItemUpdate addItem) {
        return catalogueClient.getItem(addItem.id)
            .switchIfEmpty(Single.error(() ->
                new HttpStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + addItem.id)
            )).flatMapCompletable((product ->
                    client.postCart(cartId, Map.of(
                           "customerId", authentication == null ? "" : MuUserDetails.resolveId(authentication),
                           "items", Collections.singletonList(Map.of(
                                    ITEM_ID, product.id,
                                    UNIT_PRICE, product.price,
                                    QUANTITY, addItem.quantity
                            ))
                    )).flatMapCompletable(httpStatus -> {
                        if (httpStatus.getCode() > 201) {
                            return Completable.error(new HttpStatusException(httpStatus, "Unable to add to cart"));
                        }
                        return Completable.complete();
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
    Completable updateItem(
            @Parameter(hidden = true) @CartId UUID cartId,
            @Body ItemUpdate addItem) {
        return catalogueClient.getItem(addItem.id)
                .switchIfEmpty(Single.error(() ->
                        new HttpStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + addItem.id)
                )).flatMapCompletable((product ->
                        client.updateCartItem(cartId, Map.of(
                                ITEM_ID, product.id,
                                UNIT_PRICE, product.price,
                                QUANTITY, addItem.quantity
                        )).flatMapCompletable(httpStatus -> {
                            if (httpStatus.getCode() > 201) {
                                return Completable.error(new HttpStatusException(httpStatus, "Unable to update to cart"));
                            }
                            return Completable.complete();
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
    Completable deleteCartItem( @Parameter(hidden = true) @CartId UUID cartID, String id) {
        return client.deleteCartItem(cartID, id);
    }

    @Client(id = ServiceLocator.CATALOGUE, path = "/catalogue")
    public interface CatalogueClient {
        @Get("/{id}")
        Maybe<Product> getItem(String id);
    }

    @Client(id = ServiceLocator.CARTS, path = "/carts")
    interface CartsClient {
        @Get(uri = "/{cartId}/items", produces = MediaType.APPLICATION_JSON)
        Single<List<CartItem>> getCartItems(UUID cartId);

        @Delete(uri = "/{cartId}")
        Completable deleteCart(UUID cartId);

        @Delete(uri = "/{cartId}/items/{itemId}", produces = MediaType.APPLICATION_JSON)
        Completable deleteCartItem(UUID cartId, String itemId);

        @Post(uri = "/{cartId}", processes = MediaType.APPLICATION_JSON)
        Single<HttpStatus> postCart(UUID cartId, @Body Map<String, Object> body);

        @Put(uri = "/{cartId}/items", processes = MediaType.APPLICATION_JSON)
        Single<HttpStatus> updateCartItem(UUID cartId, @Body Map<String, Object> body);
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

        private String itemId;

        private final int quantity;

        private BigDecimal unitPrice;

        public CartItem() {
            id = UUID.randomUUID().toString();
            quantity = 1;
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
