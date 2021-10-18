package api.services.exceptions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.retry.annotation.RetryPredicate;

/**
 * Predicate used by {@link api.services.annotation.MuService} {@link io.micronaut.retry.annotation.CircuitBreaker} to
 * decide whether to apply the retry logic or not.
 */
@Introspected
public class ClientResponsePredicate implements RetryPredicate {
    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof HttpClientResponseException) {
            // don't retry on 404s and redirects
            return ((HttpClientResponseException) throwable).getResponse().status().getCode() >= 500;
        }
        return true;
    }
}
