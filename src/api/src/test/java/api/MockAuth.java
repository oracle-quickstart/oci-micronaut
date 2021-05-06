package api;

import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.AuthClient;
import io.reactivex.Single;

import java.util.UUID;

class MockAuth implements AuthClient {

    String userId = UUID.randomUUID().toString();

    @Override
    public Single<MuUserDetails> login(String username, String password) {
        return Single.just(new MuUserDetails(userId, username));
    }

    @Override
    public Single<MuUserDetails> register(UserRegistrationRequest registrationRequest) {
        return Single.just(new MuUserDetails(userId, registrationRequest.getUsername()));
    }
}
