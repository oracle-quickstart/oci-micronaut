package api.services;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.session.SessionLoginHandler;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.validation.Valid;
import java.util.Map;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UsersService {
    private final UsersClient client;
    private final AuthClient authClient;
    private final SessionLoginHandler sessionLoginHandler;

    UsersService(UsersClient client, AuthClient authClient, SessionLoginHandler sessionLoginHandler) {
        this.client = client;
        this.authClient = authClient;
        this.sessionLoginHandler = sessionLoginHandler;
    }

    @Post("/register")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @TrackEvent("user:register")
    Single<MuUserDetails> register(HttpRequest<?> request, @Valid @Body UserRegistrationRequest registrationRequest) {
        return authClient.register(registrationRequest)
                .map((userDTO -> {
                    sessionLoginHandler.loginSuccess(userDTO, request);
                    return userDTO;
                }));
    }

    @Get("/profile")
    Maybe<Map<String, Object>> getProfile(Authentication auth) {
        return client.getUser(MuUserDetails.resolveId(auth));
    }

    @Get("/customers/{id}")
    Maybe<Map<String, Object>> getProfile(String id, Authentication auth) {
        final String authId = MuUserDetails.resolveId(auth);
        if (id.equals(authId)) {
            return client.getUser(authId);
        } else {
            return Maybe.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
    }

    @Post("/address")
    @TrackEvent("create:address")
    Single<AddressInfo> addAddress(Authentication authentication, @Body AddressInfo body) {
        return client.addAddress(MuUserDetails.resolveId(authentication), body);
    }

    @Get("/address")
    @TrackEvent("get:address")
    Single<AddressInfo> getAddress(Authentication authentication) {
        return client.getAddresses(MuUserDetails.resolveId(authentication)).firstOrError();
    }

    @Post("/card")
    @TrackEvent("create:card")
    Single<CardInfo> addCard(Authentication authentication, @Body CardInfo body) {
        return client.addCard(MuUserDetails.resolveId(authentication), body);
    }

    @Delete("/card/{cardId}")
    @TrackEvent("delete:card")
    @Status(HttpStatus.NO_CONTENT)
    Completable deleteCard(Authentication authentication, String cardId) {
        return client.deleteCard(MuUserDetails.resolveId(authentication), cardId);
    }

    @Delete("/address/{addressId}")
    @TrackEvent("delete:address")
    @Status(HttpStatus.NO_CONTENT)
    Completable deleteAddress(Authentication authentication, String addressId) {
        return client.deleteAddress(MuUserDetails.resolveId(authentication), addressId);
    }

    @Get("/card")
    @TrackEvent("get:card")
    Single<CardInfo> getCard(Authentication authentication) {
        return client.getCards(MuUserDetails.resolveId(authentication)).firstOrError();
    }

}
