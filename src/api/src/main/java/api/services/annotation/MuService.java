package api.services.annotation;

import api.services.exceptions.ClientResponsePredicate;
import io.micronaut.http.annotation.Controller;
import io.micronaut.retry.annotation.CircuitBreaker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Meta-annotation that groups the {@link CircuitBreaker} and {@link Controller}.
 */
@Retention(RetentionPolicy.RUNTIME)
@CircuitBreaker(predicate = ClientResponsePredicate.class)
@Controller("/api")
public @interface MuService {
}
