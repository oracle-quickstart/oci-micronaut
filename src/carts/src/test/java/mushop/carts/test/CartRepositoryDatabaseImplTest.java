package mushop.carts.test;

import io.micronaut.core.util.StringUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import mushop.carts.entities.Cart;
import mushop.carts.repositories.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartRepositoryDatabaseImplTest implements TestPropertyProvider {

    @Container
    static OracleContainer oracleContainer = new OracleContainer("registry.gitlab.com/micronaut-projects/micronaut-graal-tests/oracle-database:18.4.0-xe");

    @Inject
    CartRepository cartRepository;

    @Test
    void testCartRepository() {
        assertTrue(cartRepository.healthCheck());

        cartRepository.save(new Cart("1234"));
        final Cart cart = cartRepository.getById("1234");
        assertNotNull(cart);
        assertEquals("1234", cart.getId());
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        oracleContainer.start();
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
