package api.services;

import api.Application;
import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.UserDetail;
import api.model.UserRegistrationRequest;
import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.session.SessionLoginHandler;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UsersService {

    public static final Logger LOG = LoggerFactory.getLogger(UsersService.class);

    private final UsersClient client;
    private final AuthClient authClient;
    private final SessionLoginHandler sessionLoginHandler;

    UsersService(UsersClient client,
                 AuthClient authClient,
                 SessionLoginHandler sessionLoginHandler) {
        this.client = client;
        this.authClient = authClient;
        this.sessionLoginHandler = sessionLoginHandler;
    }

    @Post("/register")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @TrackEvent("user:register")
    @Operation(
            summary = "Register user",
            description = "Creates user account.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful registration."),
                    @ApiResponse(responseCode = "409", description = "User already exists.")
            },
            tags = {"user"}
    )
    public Single<MuUserDetails> register(HttpRequest<?> request,
                                          UserRegistrationRequest registrationRequest) {
        return authClient.register(registrationRequest)
                .doOnError(throwable -> LOG.error("Failed to register user: " + throwable.getMessage(), throwable))
                .map((userDTO -> {
                    sessionLoginHandler.loginSuccess(userDTO, request);
                    return userDTO;
                }))
                .doOnError(throwable -> LOG.error("Failed to create user session: " + throwable.getMessage(), throwable));
    }

    @Get("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Loads the user profile.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns user profile."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"user"})
    public Maybe<UserDetail> getProfile(Authentication auth) {
        return client.getUser(MuUserDetails.resolveId(auth));
    }

    @Operation(
            summary = "Get user profile",
            description = "Loads the profile of user specified by %id%.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            parameters = {
                    @Parameter(name = "id", description = "User uuid", example = "a6bd188f-bf9a-4f0f-a9f0-6573dd89419d", in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns user profile."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "User profile not found.")
            },
            tags = {"user"})
    @Get("/customers/{id}")
    public Maybe<UserDetail> getProfile(String id, Authentication auth) {
        final String authId = MuUserDetails.resolveId(auth);
        if (id.equals(authId)) {
            return client.getUser(authId);
        } else {
            return Maybe.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
    }

    @Operation(
            summary = "Add user address",
            description = "Creates new user address.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns new user address."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"user"})
    @Post("/address")
    @TrackEvent("create:address")
    public Single<AddressInfo> addAddress(Authentication authentication, @Body AddressInfo body) {
        return client.addAddress(MuUserDetails.resolveId(authentication), body);
    }

    @Operation(
            summary = "Get user address",
            description = "Get user latest address.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns user latest address."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"user"})
    @Get("/address")
    @TrackEvent("get:address")
    public Single<AddressInfo> getAddress(Authentication authentication) {
        return client.getAddresses(MuUserDetails.resolveId(authentication)).firstOrError();
    }

    @Operation(
            summary = "Create card",
            description = "Creates new payment card.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns new card info. The return values are redacted."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"card"})
    @Post("/card")
    @TrackEvent("create:card")
    public Single<CardInfo> addCard(Authentication authentication, @Body CardInfo body) {
        return client.addCard(MuUserDetails.resolveId(authentication), body);
    }

    @Operation(
            summary = "Delete card",
            description = "Deletes payment card.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            parameters = {
                    @Parameter(name = "cardId", description = "Card id", example = "22", in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Card successfully deleted."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "Card not found.")
            },
            tags = {"card"})
    @Delete("/card/{cardId}")
    @TrackEvent("delete:card")
    @Status(HttpStatus.NO_CONTENT)
    public Completable deleteCard(Authentication authentication, String cardId) {
        return client.deleteCard(MuUserDetails.resolveId(authentication), cardId);
    }

    @Operation(
            summary = "Delete address",
            description = "Deletes user address.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            parameters = {
                    @Parameter(name = "addressId", description = "Address id", example = "a6bd188f-bf9a-4f0f-a9f0-6573dd89419d", in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Address successfully deleted."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "Address not found.")
            },
            tags = {"user"})
    @Delete("/address/{addressId}")
    @TrackEvent("delete:address")
    @Status(HttpStatus.NO_CONTENT)
    public Completable deleteAddress(Authentication authentication, String addressId) {
        return client.deleteAddress(MuUserDetails.resolveId(authentication), addressId);
    }

    @Operation(
            summary = "Get card",
            description = "Get user payment card. The return values are redacted.",
            security = @SecurityRequirement(name = Application.COOKIE_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns card info."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            },
            tags = {"card"})

    @Get("/card")
    @TrackEvent("get:card")
    public Single<CardInfo> getCard(Authentication authentication) {
        return client.getCards(MuUserDetails.resolveId(authentication)).firstOrError();
    }
}
