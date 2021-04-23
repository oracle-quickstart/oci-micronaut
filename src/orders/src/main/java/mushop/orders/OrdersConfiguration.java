package mushop.orders;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;

/**
 * Orders configuration.
 */
@ConfigurationProperties("orders")
public interface OrdersConfiguration {

    @Bindable(defaultValue = "5")
    int getTimeout();
}
