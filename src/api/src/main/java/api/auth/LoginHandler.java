package api.auth;

import api.services.AuthClient;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;

@Singleton
public class LoginHandler implements AuthenticationProvider {
    private final AuthClient client;

    public LoginHandler(AuthClient client) {
        this.client = client;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {
        if (authenticationRequest instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) authenticationRequest;
            return client.login(credentials.getUsername(), credentials.getPassword())
                    .cast(AuthenticationResponse.class)
                    .toFlowable();
        }
        return Flowable.just(new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }
}
