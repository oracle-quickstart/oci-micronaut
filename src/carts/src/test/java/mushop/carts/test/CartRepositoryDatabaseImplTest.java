package mushop.carts.test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mushop.carts.entities.Cart;
import mushop.carts.repositories.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartRepositoryDatabaseImplTest implements OracleSodaTest {

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

    @Override
    public OracleContainer getOracleContainer() {
        return oracleContainer;
    }
}