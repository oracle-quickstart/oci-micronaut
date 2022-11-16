package api;

import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.AuthClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

class MockAuth implements AuthClient {

    String userId = UUID.randomUUID().toString();

    @Override
    public Mono<MuUserDetails> login(String username, String password) {
        return Mono.just(new MuUserDetails(userId, username));
    }

    @Override
    public Mono<MuUserDetails> register(UserRegistrationRequest registrationRequest) {
        return Mono.just(new MuUserDetails(userId, registrationRequest.getUsername()));
    }
}
