package catalogue;

import io.micronaut.runtime.Micronaut;

public class AwsApplication {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
