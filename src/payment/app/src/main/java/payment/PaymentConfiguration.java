package payment;

import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Configuration for the payment service.
 */
@ConfigurationProperties("payment")
public interface PaymentConfiguration {
    double getDeclineAmount();
}
