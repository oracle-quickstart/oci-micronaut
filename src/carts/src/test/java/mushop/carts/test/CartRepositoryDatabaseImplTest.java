package mushop.carts.test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
import mushop.carts.repositories.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartRepositoryDatabaseImplTest implements OracleSodaTest {

    @Container
    static OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-xe:slim")
            .withEnv("ORACLE_PASSWORD", "oracle");

    @Inject
    CartRepository cartRepository;

    @Test
    void testCartRepository() {
        assertTrue(cartRepository.healthCheck());

        cartRepository.save(new Cart("1234"));
        Cart cart = cartRepository.getById("1234");
        assertNotNull(cart);
        assertEquals("1234", cart.getId());
        assertEquals(0, cart.getItems().size());

        Item item1 = new Item();
        item1.setItemId("1");
        item1.setQuantity(1);
        item1.setUnitPrice(BigDecimal.valueOf(22));
        item1.setId("1");
        cart.getItems().add(item1);
        cartRepository.update(cart);

        cart = cartRepository.getById("1234");
        assertNotNull(cart);
        assertEquals("1234", cart.getId());

        cartRepository.deleteCart("1234");
    }

    @Override
    public OracleContainer getOracleContainer() {
        return oracleContainer;
    }
}