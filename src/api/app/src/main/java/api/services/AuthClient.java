package api.services;

import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

/**
 * The authentication client.
 */
@Client(id = ServiceLocator.USER)
public interface AuthClient {
    @Post("/login")
    Mono<MuUserDetails> login(String username, String password);

    @Post("/register")
    Mono<MuUserDetails> register(@Body UserRegistrationRequest registrationRequest);
}
