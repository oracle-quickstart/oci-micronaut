package api.services;

import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client(id = ServiceLocator.USER)
public interface AuthClient {
    @Post("/login")
    Single<MuUserDetails> login(String username, String password);

    @Post("/register")
    Single<MuUserDetails> register(@Body UserRegistrationRequest registrationRequest);
}
