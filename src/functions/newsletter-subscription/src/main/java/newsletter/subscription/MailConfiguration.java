package newsletter.subscription;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("smtp")
public interface MailConfiguration {
    /**
     * @return The SMTP user
     */
    @NotBlank
    String getUser();

    /**
     * @return The SMTP password
     */
    @NotBlank
    String getPassword();

    /**
     * @return The SMTP host
     */
    @NotBlank
    String getHost();

    /**
     * @return The SMTP port
     */
    @Bindable(defaultValue = "587")
    int getPort();

}
