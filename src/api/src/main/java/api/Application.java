package api;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "mushop-api",
                version = "1.0",
                description = "Micronaut MuShop API"
        )
)
@SecurityScheme(type = SecuritySchemeType.HTTP, name = Application.BASIC_AUTH, scheme = "basic")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = Application.COOKIE_AUTH, in = SecuritySchemeIn.COOKIE, paramName = "SESSION")
public class Application {

    public static final String BASIC_AUTH = "BasicAuth";
    public static final String COOKIE_AUTH = "CookieAuth";

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
