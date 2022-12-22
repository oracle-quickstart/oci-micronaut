package catalogue;

import catalogue.controllers.CatalogueItemDTO;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractCatalogueTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject CatalogueClient catalogueClient;

    @Test
    void testItWorks() {
        assertTrue(application.isRunning());
    }

    @Test
    void testListCatalogueResources() {
        final HttpResponse<List<CatalogueItemDTO>> response = catalogueClient.list(null, null);
        assertTrue(response.getBody().isPresent());
    }

    @Test
    void testListCatalogueResourcesWithSize() {
        final HttpResponse<List<CatalogueItemDTO>> response = catalogueClient.list(null, 10);
        assertTrue(response.getBody().isPresent());
        assertEquals(10, response.getBody().orElseThrow().size(), 10);
    }

    @Client("/")
    interface CatalogueClient {
        @Get("/catalogue{?categories,size}")
        HttpResponse<List<CatalogueItemDTO>> list(@Nullable List<String> categories, @Nullable Integer size);
    }
}
