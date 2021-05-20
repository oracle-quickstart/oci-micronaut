package payment;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class PaymentTest {

    @Test
    @Property(name = "payment.decline.amount", value = "100")
    void testAuthorize(PaymentClient client) {
        final Authorization authorization = client.paymentAuth(new AuthorizationRequest(50));
        assertTrue(authorization.isAuthorised());
    }

    @Test
    @Property(name = "payment.decline.amount", value = "10")
    void testFailOverCertainAmount(PaymentClient client) {
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.paymentAuth(new AuthorizationRequest(50))
        );
        final Authorization authorization = exception.getResponse().getBody(Authorization.class).get();

        assertFalse(authorization.isAuthorised());
        assertEquals(authorization.getMessage(), "Payment declined: amount exceeds 10.00");
    }

    @Test
    void testFailIfAmountIsZero(PaymentClient client) {
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.paymentAuth(new AuthorizationRequest(0))
        );
        final Authorization authorization = exception.getResponse().getBody(Authorization.class).get();

        assertFalse(authorization.isAuthorised());
        assertEquals(authorization.getMessage(), "Payment declined: Amount cannot be zero");
    }

    @Test
    void testFailIfAmountIsNegative(PaymentClient client) {
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.paymentAuth(new AuthorizationRequest(-10))
        );
        final Authorization authorization = exception.getResponse().getBody(Authorization.class).get();

        assertFalse(authorization.isAuthorised());
        assertEquals(authorization.getMessage(), "Payment declined: Amount cannot be negative");
    }

    @Client(value = "/", errorType = Authorization.class)
    interface PaymentClient {
        @Post(uri = "/paymentAuth", processes = MediaType.APPLICATION_JSON)
        Authorization paymentAuth(@Body AuthorizationRequest authorizationRequest);
    }
}
