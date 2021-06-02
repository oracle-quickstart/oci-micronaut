package mushop.carts;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.micronaut.core.annotation.TypeHint;
import org.glassfish.json.JsonProviderImpl;

@OpenAPIDefinition(
        info = @Info(
                title = "cart-api",
                version = "1.0"
        )
)
@TypeHint(value = {
        JsonProviderImpl.class
}, typeNames = {
        "oracle.sql.AnyDataFactory",
        "oracle.sql.Datum",
        "oracle.sql.TypeDescriptorFactory",
        "oracle.sql.json.OracleJsonDatum",
        "oracle.sql.json.OracleJsonFactory",
        "oracle.sql.json.OracleJsonGenerator",
        "oracle.sql.json.OracleJsonParser",
        "oracle.sql.json.OracleJsonValue",
}, accessType = TypeHint.AccessType.ALL_PUBLIC)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
