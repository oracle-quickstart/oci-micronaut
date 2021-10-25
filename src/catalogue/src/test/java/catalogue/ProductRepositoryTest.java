package catalogue;

import catalogue.model.Product;
import catalogue.repositories.ProductRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class ProductRepositoryTest {

    @Inject
    ProductRepository repository;

    @Test
    void testListProducts() {
        final List<Product> products = repository.findAll();

        assertFalse(products.isEmpty());

        Product product1 = products.stream().filter(product -> product.getSku().equals("MU-US-001")).findFirst().get();

        assertNotNull(product1.getCategories());
        assertEquals(
                1,
                product1.getCategories().size()
        );

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
