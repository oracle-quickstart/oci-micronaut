/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package api.auth;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEvent;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.BasicAuthUtils;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Custom implementation of login controller for MuShop API.
 */
@Requires(beans = LoginHandler.class)
@Controller("/api/login")
@Secured(SecurityRule.IS_ANONYMOUS)
@Validated
class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    protected final Authenticator authenticator;
    protected final LoginHandler loginHandler;
    protected final ApplicationEventPublisher<ApplicationEvent> eventPublisher;

    /**
     * @param authenticator  {@link Authenticator} collaborator
     * @param loginHandler   A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher The application event publisher
     */
    LoginController(Authenticator authenticator,
                    LoginHandler loginHandler,
                    ApplicationEventPublisher<ApplicationEvent> eventPublisher) {
        this.authenticator = authenticator;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
    }

    @Operation(
            summary = "User login",
            description = "Logs user into MuShop.",
            security = @SecurityRequirement(name = "BasicAuth"),
            responses = {
                    @ApiResponse(responseCode = "303", description = "Successfully authenticated",
                            headers = @Header(required = true, name = "Set-Cookie", description = "Set session cookie")),
                    @ApiResponse(responseCode = "400", description = "Missing Authorization header."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            },
            tags = {"user"})
    @Post(consumes = {MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Status(HttpStatus.SEE_OTHER)
    Mono<MutableHttpResponse<?>> login(@Parameter(hidden = true) HttpRequest<?> request) {
        Optional<UsernamePasswordCredentials> credentials = request.getHeaders().getAuthorization().flatMap(BasicAuthUtils::parseCredentials);
        if (credentials.isPresent()) {
            Flux<AuthenticationResponse> authenticationResponseFlowable = Flux.from(authenticator.authenticate(request, credentials.get()));
            return authenticationResponseFlowable
                    .map(authenticationResponse -> {
                        LOG.info("Login response: {} {}",
                                credentials.orElse(new UsernamePasswordCredentials("n/a", "n/a")).getUsername(),
                                authenticationResponse.isAuthenticated());
                        if (authenticationResponse.isAuthenticated() && authenticationResponse.isAuthenticated()) {
                            Authentication authentication = authenticationResponse.getAuthentication().get();
                            eventPublisher.publishEvent(new LoginSuccessfulEvent(authentication));
                            return loginHandler.loginSuccess(authentication, request);
                        } else {
                            eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse));
                            return loginHandler.loginFailed(authenticationResponse, request);
                        }
                    })
                    .onErrorReturn(HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR))
                    .single(HttpResponse.status(HttpStatus.UNAUTHORIZED));
        } else {
            return Mono.just(HttpResponse.status(HttpStatus.BAD_REQUEST));
        }
    }
}
