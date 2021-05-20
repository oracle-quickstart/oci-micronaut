package catalogue;

import catalogue.model.Product;
import catalogue.repositories.ProductRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class ProductRepositoryTest {

    @Inject
    ProductRepository repository;

    @Test
    void testListProducts() {
        final List<Product> products = repository.findAll();

        assertFalse(products.isEmpty());

        final Page<Product> paged = repository.findAll(
                Pageable.from(2, 5).order(Sort.Order.asc("brand"))
        );

        assertEquals(
                5,
                paged.getContent().size()
        );
    }

    @Test
    void testGetProductById() {
        Optional<Product> product = repository.findById("MU-US-002");

        assertTrue(product.isPresent());
    }
}
