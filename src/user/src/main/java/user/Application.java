package user;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/*
 * This annotation triggers the generation of OpenAPI documentation
 */
@OpenAPIDefinition(
        info = @Info(
                title = "user",
                version = "1.0"
        )
)
public class Application {
    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        // starts the Micronaut server
        Micronaut.run(Application.class, args);
    }
}
