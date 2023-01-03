package api;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractAssetsServiceTest extends AbstractDatabaseServiceTest {

    @Inject
    AssetsApiClient assetsApiClient;

    @NonNull
    @Override
    public Map<String, String> getProperties() {
        boolean useMongoDB = false;
        boolean useNats = false;
        return getProperties(useMongoDB, useNats);
    }

    @Override
    protected String getServiceVersion() {
        return "2.0.0-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "assets";
    }

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
