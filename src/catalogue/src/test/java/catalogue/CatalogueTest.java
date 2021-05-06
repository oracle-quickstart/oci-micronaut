package catalogue;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class CatalogueTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject CatalogueClient catalogueClient;

    @Test
    void testItWorks() {
        assertTrue(application.isRunning());
    }

    @Test
    void testStaticResources() {
        final HttpResponse<byte[]> response = catalogueClient.getImage("MU-US-001.png");

        assertTrue(response.getBody().isPresent());

        assertEquals(MediaType.IMAGE_PNG_TYPE, response.getContentType().get());
    }

    @Client("/")
    interface CatalogueClient {
        @Get("/catalogue/images/{name}")
        HttpResponse<byte[]> getImage(String name);

    }
}
