package api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Maybe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CatalogueServiceTest extends AbstractDatabaseServiceTest {

    @Inject
    CatalogueApiClient catalogueApiClient;


    @Test
    void testListCategories() {
        final Map<String, Object> categories = catalogueApiClient.getCategories();
        assertNotNull(categories);
        assertTrue(categories.containsKey("categories"));
    }

    @Test
    void testGetItem() {
        final Map<String, Object> notThere = catalogueApiClient.getItem("junk").blockingGet();
        assertNull(notThere);

        final Map<String, Object> item = catalogueApiClient.getItem("MU-US-002").blockingGet();

        assertNotNull(item);
        assertTrue(item.containsKey("brand"));
    }

    @Test
    void testStaticResources() {
        final HttpResponse<byte[]> response = catalogueApiClient.getImage("MU-US-001.png");

        assertTrue(response.getBody().isPresent());

        assertEquals(MediaType.IMAGE_PNG_TYPE, response.getContentType().get());
    }


    @Override
    protected String getServiceVersion() {
        return "1.0.0-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "catalogue";
    }

    @Client("/api")
    interface CatalogueApiClient {
        @Get("/catalogue/{id}")
        Maybe<Map<String, Object>> getItem(String id);

        @Get("/categories")
        Map<String, Object> getCategories();

        @Get(value = "/catalogue/images/{name}", processes = MediaType.IMAGE_PNG)
        HttpResponse<byte[]> getImage(String name);
    }
}
