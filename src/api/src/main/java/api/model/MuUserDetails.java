package api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Schema(title = "Authentication details")
@Introspected
public class MuUserDetails extends UserDetails {

    public static final String ID = "id";

    private final String id;

    public MuUserDetails(String id, String username) {
        super(username, Collections.emptyList(), Collections.singletonMap(ID, id));
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Optional<UserDetails> getUserDetails() {
        return super.getUserDetails();
    }

    /**
     * Resolves user id from authentication.
     *
     * @param auth the authentication
     * @return user id
     * @throws NullPointerException when user is not authenticated
     */
    public static String resolveId(Authentication auth) {
        return Objects.requireNonNull(auth.getAttributes().get(ID), "User ID should never be null")
                .toString();
    }
}
