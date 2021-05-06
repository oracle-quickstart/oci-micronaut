package api.services;

import api.model.MuUserDetails;
import api.model.Product;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import api.services.annotation.CartId;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.*;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CartsService {
    private static final String ITEM_ID = "itemId";
    private static final String UNIT_PRICE = "unitPrice";
    private static final String QUANTITY = "quantity";
    private final CartsClient client;
    private final CatalogueClient catalogueClient;

    public CartsService(CartsClient client, CatalogueClient catalogueClient) {
        this.client = client;
        this.catalogueClient = catalogueClient;
    }

    @Get(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Single<List<?>> getCart(@CartId UUID cartID) {
        return client.getCartItems(cartID)
                .onErrorReturnItem(Collections.emptyList());
    }

    @Delete(value = "/cart", produces = MediaType.APPLICATION_JSON)
    @Status(HttpStatus.NO_CONTENT)
    @TrackEvent("delete:cart")
    Completable deleteCart(@CartId UUID cartID) {
        return client.deleteCart(cartID)
                    .onErrorComplete();
    }

    @Status(HttpStatus.CREATED)
    @Post(value = "/cart")
    @TrackEvent("cart:addItem")
    Completable addItem(
            Authentication authentication,
            @CartId UUID cartId,
            @Body ItemUpdate addItem) {
        return catalogueClient.getItem(addItem.id)
            .switchIfEmpty(Single.error(() ->
                new HttpStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + addItem.id)
            )).flatMapCompletable((product ->
                    client.postCart(cartId, Map.of(
                           "customerId", MuUserDetails.resolveId(authentication),
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

    @Status(HttpStatus.OK)
    @Post(value = "/cart/update")
    Completable updateItem(
            @CartId UUID cartId,
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

    @Delete(value = "/cart/{id}", produces = MediaType.APPLICATION_JSON)
    @Status(HttpStatus.NO_CONTENT)
    @TrackEvent("delete:cartItem")
    Completable deleteCartItem(@CartId UUID cartID, String id) {
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
        Single<List<?>> getCartItems(UUID cartId);

        @Delete(uri = "/{cartId}")
        Completable deleteCart(UUID cartId);

        @Delete(uri = "/{cartId}/items/{itemId}", produces = MediaType.APPLICATION_JSON)
        Completable deleteCartItem(UUID cartId, String itemId);

        @Post(uri = "/{cartId}", processes = MediaType.APPLICATION_JSON)
        Single<HttpStatus> postCart(UUID cartId, @Body Map<String, Object> body);

        @Put(uri = "/{cartId}/items", processes = MediaType.APPLICATION_JSON)
        Single<HttpStatus> updateCartItem(UUID cartId, @Body Map<String, Object> body);
    }

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
    }

}
