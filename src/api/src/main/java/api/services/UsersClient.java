package api.services;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.UserDetail;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The MuShop user client.
 */
@Client(id = ServiceLocator.USER, path = "/customers")
public interface UsersClient {

    @Get("/{customerId}")
    Mono<UserDetail> getUser(String customerId);

    @Post("/{customerId}/addresses")
    Mono<AddressInfo> addAddress(String customerId, @Body AddressInfo address);

    @Get("/{customerId}/addresses")
    Flux<AddressInfo> getAddresses(String customerId);

    @Post("/{customerId}/cards")
    Mono<CardInfo> addCard(String customerId, @Body CardInfo card);

    @Get("/{customerId}/cards")
    Flux<CardInfo> getCards(String customerId);

    @Delete("/{customerId}/cards/{cardId}")
    Mono<Void> deleteCard(String customerId, String cardId);

    @Delete("/{customerId}/addresses/{addressId}")
    Mono<Void> deleteAddress(String customerId, String addressId);

    @Get("/{customerId}")
    Mono<MuUserDetails> getProfile(String customerId);
}
