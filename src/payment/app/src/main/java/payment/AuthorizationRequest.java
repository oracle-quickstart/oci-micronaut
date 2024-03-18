package payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

/**
 * An authorization request for the payment.
 */
@Serdeable
public class AuthorizationRequest {

    private final double amount;

    public AuthorizationRequest(double amount) {
        this.amount = amount;
    }

    /**
     * The amount to authorize
     */
    @JsonProperty("Amount")
    public double getAmount() {
        return amount;
    }
}
