package api.auth;

import api.services.AuthClient;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * MuShop implementation of {@link AuthenticationProvider} that uses {@link AuthClient} to authenticate users.
 */
@Singleton
public class MuShopAuthenticationProvider implements AuthenticationProvider {

    private final AuthClient client;

    public MuShopAuthenticationProvider(AuthClient client) {
        this.client = client;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {
        if (authenticationRequest instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) authenticationRequest;
            return client.login(credentials.getUsername(), credentials.getPassword())
                    .map(muUserDetails -> AuthenticationResponse.success(credentials.getUsername(), muUserDetails.getAttributes()));
        }
        return Mono.just(new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }
}
