package mushop;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

public class AwsApplication {
    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(AwsApplication.class)
                .defaultEnvironments(Environment.AMAZON_EC2)
                .start();
    }
}
