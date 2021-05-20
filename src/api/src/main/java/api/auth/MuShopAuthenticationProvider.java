package api.auth;

import api.services.AuthClient;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.handlers.LoginHandler;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class MuShopAuthenticationProvider implements AuthenticationProvider {
    public static final Logger LOG = LoggerFactory.getLogger(MuShopAuthenticationProvider.class);
    public static final String AUTH_RESP = "AUTHENTICATION_RESPONSE";

    private final AuthClient client;
    private final LoginHandler loginHandler;
    private final ApplicationEventPublisher eventPublisher;

    public MuShopAuthenticationProvider(AuthClient client, LoginHandler loginHandler, ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
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
