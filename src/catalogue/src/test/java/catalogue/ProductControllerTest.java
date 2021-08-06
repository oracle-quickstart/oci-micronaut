package catalogue;

import catalogue.controllers.CatalogueItemDTO;
import catalogue.controllers.CatalogueOperations;
import catalogue.controllers.CatalogueSizeDTO;
import catalogue.controllers.CategoriesDTO;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class ProductControllerTest {
    @Inject
    CatalogueClient client;

    @Test
    void testList() {
        List<CatalogueItemDTO> results = client.list(Collections.emptyList(), Pageable.UNPAGED);

        assertEquals(
                27,
                results.size()
        );

        results = client.listPaged(Collections.emptyList(), "brand", 2, 5);

        assertEquals(
                5,
                results.size()
        );
    }

    @Test
    void testSize() {
        CatalogueSizeDTO size = client.size(Collections.emptyList());
        assertEquals(
                27,
                size.getSize()
        );

        size = client.size(Collections.singletonList("Deodorizers"));
        assertEquals(
                2,
                size.getSize()
        );
    }

    @Test
    void testGetById() {
        final Optional<CatalogueItemDTO> catalogueItemDTO = client.find("MU-US-003");

        assertTrue(catalogueItemDTO.isPresent());
        final CatalogueItemDTO catalogueItem = catalogueItemDTO.get();

        assertEquals("Mu DeoSpray Deodorizer", catalogueItem.getTitle());

        final Optional<CatalogueItemDTO> garbage = client.find("garbage");
        assertFalse(garbage.isPresent());
    }

    @Test
    void testListCategories() {
        CategoriesDTO results = client.listCategories();

        assertEquals(
                15,
                results.getCategories().length
        );

        results = client.listCategories();

        assertEquals(
                15,
                results.getCategories().length
        );
    }

    @Client("/")
    interface CatalogueClient extends CatalogueOperations {
        @Get("/catalogue{?categories,size,page,order}")
        List<CatalogueItemDTO> listPaged(
                @Nullable @QueryValue List<String> categories,
                @Nullable @QueryValue("order") String sort,
                @Nullable @QueryValue("page") Integer page,
                @Nullable @QueryValue("size") Integer size
        );

    }
}
