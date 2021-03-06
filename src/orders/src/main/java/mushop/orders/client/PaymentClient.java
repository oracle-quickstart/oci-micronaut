package mushop.orders.client;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import mushop.orders.resources.PaymentRequest;
import mushop.orders.resources.PaymentResponse;
import reactor.core.publisher.Mono;

/**
 * Payment service client.
 */
@Client(id = "payment")
public interface PaymentClient {

    /**
     * Sends payment request.
     *
     * @param paymentRequest payment request
     * @return payment response
     */
    @Post(uri = "/paymentAuth", processes = MediaType.APPLICATION_JSON)
    Mono<PaymentResponse> createPayment(@Body PaymentRequest paymentRequest);
}
