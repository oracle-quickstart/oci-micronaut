package mushop.orders;

import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import software.amazon.awssdk.services.sts.internal.StsWebIdentityCredentialsProviderFactory;

@TypeHint(
        value = {StsWebIdentityCredentialsProviderFactory.class},
        accessType = {TypeHint.AccessType.ALL_PUBLIC_CONSTRUCTORS}
)
public class AwsApplication {
    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(AwsApplication.class)
                .defaultEnvironments(Environment.AMAZON_EC2)
                .start();
    }
}
