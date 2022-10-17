package catalogue;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@TypeHint(
        typeNames = {
                "com.github.benmanes.caffeine.cache.BBHeader$ReadAndWriteCounterRef",
                "com.github.benmanes.caffeine.cache.BBHeader$ReadCounterRef",
                "com.github.benmanes.caffeine.cache.BLCHeader$DrainStatusRef",
                "com.github.benmanes.caffeine.cache.PS",
                "com.github.benmanes.caffeine.cache.PSA",
                "com.github.benmanes.caffeine.cache.PSAW",
                "com.github.benmanes.caffeine.cache.SSAW",
                "com.github.benmanes.caffeine.cache.StripedBuffer",
                "java.lang.Thread"},
        accessType = {
                TypeHint.AccessType.ALL_PUBLIC,
                TypeHint.AccessType.ALL_PUBLIC_CONSTRUCTORS,
                TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS,
                TypeHint.AccessType.ALL_DECLARED_FIELDS
        }
)
@OpenAPIDefinition(
    info = @Info(
            title = "catalogue-api",
            version = "1.0"
    )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(Application.class)
                .defaultEnvironments("dev")
                .start();
    }
}
