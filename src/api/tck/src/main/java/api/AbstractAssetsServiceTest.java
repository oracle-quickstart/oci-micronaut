package api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractAssetsServiceTest {

    @Inject
    AssetsApiClient assetsApiClient;

    @Test
    void testStaticResources() {
        final HttpResponse<byte[]> response = assetsApiClient.getProductImage("MU-US-001.png");

        assertTrue(response.getBody().isPresent());

        assertEquals(MediaType.IMAGE_PNG_TYPE, response.getContentType().get());
    }

    @Client("/api")
    interface AssetsApiClient {
        @Get(value = "/assets/image/product/{name}", processes = MediaType.IMAGE_PNG)
        HttpResponse<byte[]> getProductImage(String name);
    }
}
