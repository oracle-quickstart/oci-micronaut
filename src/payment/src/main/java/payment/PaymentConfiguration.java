package payment;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("payment")
public interface PaymentConfiguration {
    double getDeclineAmount();
}
