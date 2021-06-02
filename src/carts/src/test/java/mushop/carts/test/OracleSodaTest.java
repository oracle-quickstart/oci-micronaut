package mushop.carts.test;

import io.micronaut.core.util.StringUtils;
import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.containers.OracleContainer;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public interface OracleSodaTest extends TestPropertyProvider {

    OracleContainer getOracleContainer();

    @Nonnull
    @Override
    default Map<String, String> getProperties() {
        final OracleContainer oracleContainer = getOracleContainer();
        try (Connection connection = oracleContainer.createConnection("")) {
            try (final PreparedStatement ps = connection.prepareStatement("GRANT SODA_APP TO " + oracleContainer.getUsername())) {
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to setup SODA: " + e.getMessage(), e);
        }
        return Map.of(
                "datasources.default.url", oracleContainer.getJdbcUrl(),
                "datasources.default.driverClassName", oracleContainer.getDriverClassName(),
                "datasources.default.username", oracleContainer.getUsername(),
                "datasources.default.password", oracleContainer.getPassword(),
                "datasources.default.soda.create-soda-user", StringUtils.TRUE,
                "datasources.default.soda.properties.sharedMetadataCache", StringUtils.TRUE,
                "datasources.default.soda.create-collections[0]", "cart"
        );
    }
}
