package events;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import software.amazon.awssdk.services.sts.internal.StsWebIdentityCredentialsProviderFactory;

@OpenAPIDefinition(
        info = @Info(
                title = "events-api",
                version = "1.0"
        )
)
@TypeHint(
        value = {StsWebIdentityCredentialsProviderFactory.class},
        accessType = {TypeHint.AccessType.ALL_PUBLIC_CONSTRUCTORS}
)
public class Application {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(Application.class)
                .defaultEnvironments("app")
                .start();
    }
}
