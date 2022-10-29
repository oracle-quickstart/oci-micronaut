package user;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;

public class OciApplication {
    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(OciApplication.class)
                .defaultEnvironments(Environment.ORACLE_CLOUD)
                .start();
    }
}
