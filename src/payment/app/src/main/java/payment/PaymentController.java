package payment;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;

/**
 * The payment authorization controller.
 */
@Controller
class PaymentController {

    private final PaymentConfiguration configuration;

    PaymentController(PaymentConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Payment authorization
     */
    @Post(uri = "/paymentAuth", processes = MediaType.APPLICATION_JSON)
    Authorization paymentAuth(@Body AuthorizationRequest authorizationRequest) {
        final double amount = authorizationRequest.getAmount();

        if (amount < 0) {
            throw new HttpStatusException(BAD_REQUEST,
                    new Authorization(false, "Payment declined: Amount cannot be negative"));
        }

        if (amount == 0) {
            throw new HttpStatusException(BAD_REQUEST,
                    new Authorization(false, "Payment declined: Amount cannot be zero"));
        }

        if (amount > configuration.getDeclineAmount()) {
            throw new HttpStatusException(BAD_REQUEST,
                    new Authorization(false, String.format("Payment declined: amount exceeds %.2f", configuration.getDeclineAmount())));
        }

        return new Authorization(true, "Payment authorised");
    }
}
