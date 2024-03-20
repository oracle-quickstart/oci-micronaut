package payment;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Authorization information.
 */
@Serdeable
public class Authorization {

    private final boolean authorised;
    private final String message;

    public Authorization(boolean authorised, String message) {
        this.authorised = authorised;
        this.message = message;
    }

    /**
     * Returns whether the transaction was authorized.
     */
    public boolean isAuthorised() {
        return authorised;
    }

    /**
     * Returns the authorization message.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
